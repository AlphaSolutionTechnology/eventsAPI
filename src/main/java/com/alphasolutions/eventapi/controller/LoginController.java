package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.model.UserDTO;
import com.alphasolutions.eventapi.service.UserServiceImpl;
import com.alphasolutions.eventapi.utils.JwtUtil;
import com.google.api.client.json.webtoken.JsonWebToken.Payload;
import com.google.api.client.util.Value;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final UserServiceImpl userServiceImpl;
    @Value("${client.id}")
    private String clientID;

    public JwtUtil jwtUtil;

    public LoginController(JwtUtil jwtUtil, UserServiceImpl userServiceImpl) {
        this.jwtUtil = jwtUtil;
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping("/google")
    public ResponseEntity<Map<String,Object>> authenticateWithGoogle(@RequestBody Map<String,String> body, HttpServletResponse response, @CookieValue(value = "eventToken", required = false) String existingToken) {
        String googleToken = body.get("token");
        try{
            if(existingToken != null) {
                Map<String,Object> alphaTokenVerifier = jwtUtil.extractClaim(existingToken);
                if (alphaTokenVerifier.get("error") != null) {
                    return ResponseEntity.ok(alphaTokenVerifier);
                }
            }
            Payload googlePayload = jwtUtil.verifyGoogleToken(googleToken);
            var id = googlePayload.getSubject();
            User user = userServiceImpl.userExists(id);
            String eventToken;
            if(user != null)  {
                eventToken = userServiceImpl.giveUserAnotherToken(user);
            }else{
                var email = googlePayload.get("email");
                var name = googlePayload.get("name");
                UserDTO userDTO = new UserDTO(id, (String) name, (String) email, userServiceImpl.generateUniqueCode(),null);
                eventToken = jwtUtil.generateToken(userServiceImpl.createUser(userDTO));
            }
            ResponseCookie cookie = ResponseCookie
                    .from("eventToken", eventToken)
                    .httpOnly(true)
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(7 * 24 * 60 * 60)
                    .build();
            response.addHeader("Set-Cookie", cookie.toString());
            return ResponseEntity.ok().body(jwtUtil.extractClaim(eventToken));
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }

    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String,Object>> validate(@CookieValue(value = "eventToken", required = false) String existingToken) {
        Map<String,Object> body = jwtUtil.extractClaim(existingToken);
        if(body.get("error") != null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", body.get("error")));
        }
        return ResponseEntity.ok().body(body);
    }




}
