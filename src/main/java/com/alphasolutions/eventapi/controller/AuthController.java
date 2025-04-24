package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.exception.InvalidTokenException;
import com.alphasolutions.eventapi.exception.UserAlreadyExistsException;
import com.alphasolutions.eventapi.exception.UserNotFoundException;
import com.alphasolutions.eventapi.model.UserDTO;
import com.alphasolutions.eventapi.model.UserResponseDTO;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);


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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO, HttpServletResponse response) {
        try {
            // 1. Autentica o usuário
            String token = authService.authenticate(userDTO.getEmail(), userDTO.getPassword());
            
            // 2. Busca o usuário no banco
            User user = userRepository.findByEmailWithAvatar(userDTO.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("Credenciais inválidas"));

            // 3. Cria a resposta com UserResponseDTO
            UserResponseDTO userResponse = new UserResponseDTO(user);
            
            // 4. Configura o cookie
            ResponseCookie cookie = cookieService.createCookie(token);
            response.addHeader("Set-Cookie", cookie.toString());

            return ResponseEntity.ok().body(Map.of("data", userResponse));

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
    public ResponseEntity<?> validate(@CookieValue("eventToken") String token) {
        try {
            // 1. Valida o token
            authService.authenticate(token);
            
            // 2. Extrai os claims do JWT
            Map<String, Object> claims = jwtUtil.extractClaim(token);
            
            // 3. Busca o usuário no banco (ou usa os claims)
            User user = userRepository.findById(claims.get("id").toString())
                    .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
            
            // 4. Retorna o DTO padronizado
            return ResponseEntity.ok().body(Map.of("data", new UserResponseDTO(user)));

        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
