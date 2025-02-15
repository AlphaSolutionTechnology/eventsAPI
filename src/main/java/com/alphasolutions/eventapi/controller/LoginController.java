package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.exception.UserNotFoundException;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.model.UserDTO;
import com.alphasolutions.eventapi.service.CookieService;
import com.alphasolutions.eventapi.service.UserServiceImpl;
import com.alphasolutions.eventapi.utils.JwtUtil;
import com.google.api.client.json.webtoken.JsonWebToken.Payload;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final UserServiceImpl userServiceImpl;
    private final CookieService cookieService;
    public JwtUtil jwtUtil;

    public LoginController(JwtUtil jwtUtil, UserServiceImpl userServiceImpl, CookieService cookieService) {
        this.jwtUtil = jwtUtil;
        this.userServiceImpl = userServiceImpl;
        this.cookieService = cookieService;
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO, HttpServletResponse response) {
        try {
            User user = userServiceImpl.checkEmailAndPasswordValidityAndReturnUser(userDTO.getEmail(), userDTO.getPassword());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
            }
            String eventToken = jwtUtil.generateToken(user);
            response.addHeader("Set-Cookie",cookieService.createCookie(eventToken).toString());
            return ResponseEntity.ok().body("User logged in successfullyf");

        }catch (UserNotFoundException userNotFoundException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userNotFoundException.getMessage());
        }

    }

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO, HttpServletResponse response) {
        if(userServiceImpl.isEmailAlreadyExists(userDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Já existe um usuário cadastrado com este email");
        }
        try{
            userServiceImpl.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody Map<String,String> body, HttpServletResponse response, @CookieValue(value = "eventToken", required = false) String existingToken) {
        try{
            if(existingToken != null) {
                Map<String,Object> alphaTokenVerifier = jwtUtil.extractClaim(existingToken);
                if (alphaTokenVerifier.get("error") != null) {
                    return ResponseEntity.ok(alphaTokenVerifier);
                }
            }
            Payload googlePayload = jwtUtil.verifyGoogleToken(body.get("token"));
            User user = userServiceImpl.retrieveUserById(googlePayload.getSubject());
            String eventToken;
            if(user != null)  {
                eventToken = userServiceImpl.giveUserAnotherToken(user);
            }else{
                UserDTO userDTO = userServiceImpl.prepareUserWithGoogleData(googlePayload);
                eventToken = jwtUtil.generateToken(userServiceImpl.createUser(userDTO));
            }
            response.addHeader("Set-Cookie", cookieService.createCookie(eventToken).toString());
            return ResponseEntity.ok().body(jwtUtil.extractClaim(eventToken));
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
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
