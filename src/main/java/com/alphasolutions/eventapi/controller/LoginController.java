package com.alphasolutions.eventapi.controller;

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
                return  jwtUtil.extractClaim(existingToken);

            }
            Payload googlePayload = jwtUtil.verifyGoogleToken(googleToken);
            var email = googlePayload.get("email");
            var name = googlePayload.get("name");
            var id = googlePayload.getSubject();
            UserDTO userDTO = new UserDTO(id, (String) name, (String) email,null);
            userServiceImpl.createUser(userDTO);
            String eventToken = jwtUtil.generateToken(googlePayload);
            ResponseCookie cookie = ResponseCookie
                    .from("eventToken", eventToken)
                    .httpOnly(true)
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(7 * 24 * 60 * 60)
                    .build();
            response.addHeader("Set-Cookie", cookie.toString());
            return jwtUtil.extractClaim(eventToken);
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }

    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String,Object>> validate(@CookieValue(value = "eventToken", required = false) String existingToken) {
        System.out.println(existingToken);
        System.out.println(jwtUtil.extractClaim(existingToken));
        return jwtUtil.extractClaim(existingToken);
    }

}
