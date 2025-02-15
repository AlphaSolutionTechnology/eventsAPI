package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.exception.InvalidTokenException;
import com.alphasolutions.eventapi.exception.UserAlreadyExistsException;
import com.alphasolutions.eventapi.exception.UserNotFoundException;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.model.UserDTO;
import com.alphasolutions.eventapi.service.*;
import com.alphasolutions.eventapi.utils.JwtUtil;
import com.google.api.client.json.webtoken.JsonWebToken.Payload;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final CookieService cookieService;
    private final AuthService authService;
    private final UserService userService;
    private final GoogleAuthService googleAuthService;
    public JwtUtil jwtUtil;

    public LoginController(JwtUtil jwtUtil, CookieService cookieService, AuthService authService, UserService userService, GoogleAuthService googleAuthService) {
        this.jwtUtil = jwtUtil;
        this.cookieService = cookieService;
        this.authService = authService;
        this.userService = userService;
        this.googleAuthService = googleAuthService;
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO, HttpServletResponse response) {
        try {
            String eventToken = authService.authenticate(userDTO.getEmail(), userDTO.getPassword());
            response.addHeader("Set-Cookie",cookieService.createCookie(eventToken).toString());
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
                authService.auhenticate(existingToken);
            }
            String eventToken = googleAuthService.createAccountWithGoogle(body.get("token"));
            response.addHeader("Set-Cookie", cookieService.createCookie(eventToken).toString());
            return ResponseEntity.ok().body(jwtUtil.extractClaim(eventToken));
        } catch (InvalidTokenException invalidTokenException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(invalidTokenException.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@CookieValue(value = "eventToken", required = false) String existingToken) {
        Map<String,Object> body = jwtUtil.extractClaim(existingToken);
        if(body.get("error") != null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", body.get("error")));
        }
        return ResponseEntity.ok().body(body);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@CookieValue(value = "eventToken", required = true) String existingToken, HttpServletResponse response) {
        cookieService.deleteTokenCookie(existingToken);
        response.setHeader("Set-Cookie", cookieService.deleteTokenCookie(existingToken).toString());
        return ResponseEntity.ok(Map.of("message", "Logout realizado com sucesso"));
    }

}
