package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.exception.InvalidTokenException;
import com.alphasolutions.eventapi.exception.UserAlreadyExistsException;
import com.alphasolutions.eventapi.exception.UserNotFoundException;
import com.alphasolutions.eventapi.model.UserDTO;
import com.alphasolutions.eventapi.service.*;
import com.alphasolutions.eventapi.utils.JwtUtil;
import com.alphasolutions.eventapi.repository.UserRepository;
import com.alphasolutions.eventapi.model.User; // Ensure the correct package for the User class
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CookieService cookieService;
    private final AuthService authService;
    private final UserService userService;
    private final GoogleAuthService googleAuthService;
    private final AuthorizationService authorizationService;
    public JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(JwtUtil jwtUtil, CookieService cookieService, AuthService authService, UserService userService, GoogleAuthService googleAuthService, AuthorizationService authorizationService, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.cookieService = cookieService;
        this.authService = authService;
        this.userService = userService;
        this.googleAuthService = googleAuthService;
        this.authorizationService = authorizationService;
    }

    private String buildAvatarUrl(String style, String seed) {
        return "https://api.dicebear.com/8.x/" + style + "/svg?seed=" + seed;
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO, HttpServletResponse response) {
        try {
            // 1. Autenticação
            String token = authService.authenticate(userDTO.getEmail(), userDTO.getPassword());
            
            // 2. Buscar usuário COMPLETO do banco
            User user = userRepository.findByEmailWithAvatar(userDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Credenciais inválidas"));

            // 3. Construir resposta MANUALMENTE
            Map<String, Object> userData = new LinkedHashMap<>();
            userData.put("id", user.getId());
            userData.put("name", user.getNome());
            userData.put("email", user.getEmail());
            userData.put("role", user.getRole().getRole());
            userData.put("unique_code", user.getUniqueCode());
            userData.put("avatar_style", user.getAvatarStyle()); // ← Campo crítico
            userData.put("avatar_seed", user.getAvatarSeed());   // ← Campo crítico

            String avatarUrl = "https://api.dicebear.com/7.x/" + 
                            user.getAvatarStyle() + 
                            "/svg?seed=" + 
                            user.getAvatarSeed();

            // 4. Configurar cookie
            ResponseCookie cookie = cookieService.createCookie(token);
            response.addHeader("Set-Cookie", cookie.toString());

            return ResponseEntity.ok().body(Map.of(
                "data", userData,
                "avatarUrl", avatarUrl
            ));

        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {
            // Gera um seed aleatorio baseado no email ou UUID
            String avatarSeed = userDTO.getEmail() != null ?
                userDTO.getEmail().split("@")[0] + System.currentTimeMillis() :
                UUID.randomUUID().toString();

            // Define um estilo padrao para o avatar
            String avatarStyle = "adventurer"; // estilo padrao

            // Atualiza o DTO com os dados do avatar
            userDTO.setAvatarSeed(avatarSeed);
            userDTO.setAvatarStyle(avatarStyle);

            userService.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody Map<String,String> body, HttpServletResponse response, 
        @CookieValue(value = "eventToken", required = false) String existingToken) {

        try{
            if(existingToken != null && !existingToken.isEmpty()) {
                authService.authenticate(existingToken);
                Map<String, Object> claims = jwtUtil.extractClaim(existingToken);
                String avatarUrl = buildAvatarUrl(
                    (String) claims.get("avatar_style"),
                    (String) claims.get("avatar_seed")
                );
                return ResponseEntity.ok().body(Map.of(
                    "data", claims,
                    "avatarUrl", avatarUrl
                ));
            }

            String eventToken = googleAuthService.createAccountWithGoogle(body.get("token"));
            Map<String, Object> claims = jwtUtil.extractClaim(eventToken);
            String avatarUrl = buildAvatarUrl(
                (String) claims.get("avatar_style"),
                (String) claims.get("avatar_seed")
            );

            response.setHeader("Set-Cookie", cookieService.createCookie(eventToken).toString());

            return ResponseEntity.ok().body(Map.of(
                "data", claims,
                "avatarUrl", avatarUrl
            ));
        } catch (InvalidTokenException invalidTokenException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(invalidTokenException.getMessage() + "invalid token");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@CookieValue(value = "eventToken", required = false) String existingToken) {
        try {
            authService.authenticate(existingToken);
            Map<String, Object> claims = jwtUtil.extractClaim(existingToken);
            String avatarUrl = buildAvatarUrl(
                (String) claims.get("avatar_style"), 
                (String) claims.get("avatar_seed")
            );
            
            return ResponseEntity.ok().body(Map.of(
                "data", claims,
                "avatarUrl", avatarUrl
            ));
        } catch (InvalidTokenException e) {
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
