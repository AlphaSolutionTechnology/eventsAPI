package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.exception.InvalidTokenException;
import com.alphasolutions.eventapi.exception.UserAlreadyExistsException;
import com.alphasolutions.eventapi.exception.UserNotFoundException;
import com.alphasolutions.eventapi.model.dto.UserDTO;
import com.alphasolutions.eventapi.service.*;
import com.alphasolutions.eventapi.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CookieService cookieService;
    private final AuthService authService;
    private final UserService userService;
    private final GoogleAuthService googleAuthService;
    private final AuthorizationService authorizationService;
    public JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil, CookieService cookieService, AuthService authService, UserService userService, GoogleAuthService googleAuthService, AuthorizationService authorizationService) {
        this.jwtUtil = jwtUtil;
        this.cookieService = cookieService;
        this.authService = authService;
        this.userService = userService;
        this.googleAuthService = googleAuthService;
        this.authorizationService = authorizationService;
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO, HttpServletResponse response) {
        try {
            String eventToken = authService.authenticate(userDTO.getEmail(), userDTO.getPassword());
            ResponseCookie cookie = cookieService.createCookie(eventToken);

            response.setHeader("Set-Cookie", cookie.toString());
            return ResponseEntity.ok().body(Map.of("data",jwtUtil.extractClaim(eventToken)));

        }catch (UserNotFoundException userNotFoundException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", userNotFoundException.getMessage()));
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        }

    }

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {
           userService.createUser(userDTO);
           return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody Map<String,String> body, HttpServletResponse response, @CookieValue(value = "eventToken", required = false) String existingToken) {
        try{
            if(existingToken != null && !existingToken.isEmpty()) {
                authService.authenticate(existingToken);
                return ResponseEntity.ok().body(Map.of("data",jwtUtil.extractClaim(existingToken)));
            }

            String eventToken = googleAuthService.createAccountWithGoogle(body.get("token"));
            response.setHeader("Set-Cookie", cookieService.createCookie(eventToken).toString());

            return ResponseEntity.ok().body(jwtUtil.extractClaim(eventToken));
            
        } catch (InvalidTokenException invalidTokenException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(invalidTokenException.getMessage() + "invalid token");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@CookieValue(value = "eventToken", required = false) String existingToken) {
        try{
            authService.authenticate(existingToken);
            return ResponseEntity.ok().body(jwtUtil.extractClaim(existingToken));
        } catch (InvalidTokenException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@CookieValue(value = "eventToken", required = true) String existingToken, HttpServletResponse response) {
        try {
            authService.authenticate(existingToken);
            ResponseCookie eventToken = cookieService.deleteTokenCookie();
            response.setHeader("Set-Cookie", eventToken.toString());
            return ResponseEntity.ok(Map.of("message", "Logout realizado com sucesso"));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }

    }

}
