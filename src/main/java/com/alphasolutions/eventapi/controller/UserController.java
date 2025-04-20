package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.exception.UserNotFoundException;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import com.alphasolutions.eventapi.utils.JwtUtil; 

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final JwtUtil jwtUtil;

    private final UserService userService;
    private static final List<String> ALLOWED_AVATAR_STYLES = List.of(
        "adventurer", 
        "big-ears", 
        "botts", 
        "pixel-art"
    );

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    // UserController.java
    @PutMapping("/users/{userId}/avatar")
    public ResponseEntity<?> updateAvatarStyle(
        @PathVariable Long userId,
        @RequestBody Map<String, String> request
    ) {
        try {
            String newStyle = request.get("avatarStyle");
            userService.updateUserAvatarStyle(String.valueOf(userId), newStyle);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Estilo inválido. Use: adventurer, pixel-art, bottts, lorelei");
        }
    }

    @GetMapping("/avatar/styles")
    public ResponseEntity<List<String>> getAvailableAvatarStyles() {
        return ResponseEntity.ok(ALLOWED_AVATAR_STYLES);
    }

    @GetMapping("/me")
public ResponseEntity<Map<String, Object>> getCurrentUser(
    @RequestHeader("Authorization") String authHeader) {
    
    try {
        String token = authHeader.replace("Bearer ", "");
        // 1. Verifique se o token é válido
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid token"));
        }

        // 2. Obtenha o usuário
        User user = userService.getUserByToken(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "User not found"));
        }

        // 3. Construa a resposta
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("avatarUrl", user.getAvatarUrl());
        response.put("avatarSeed", user.getAvatarSeed());
        response.put("avatarStyle", user.getAvatarStyle());
        

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(response);
            
    } catch (Exception e) {
        return ResponseEntity.internalServerError()
            .body(Map.of("error", "Internal server error"));
    }
}

    @GetMapping("/avatar/preview")
    public ResponseEntity<String> getAvatarPreview(
            @RequestParam String seed,
            @RequestParam String style) {
        
        // Verificação manual do estilo
        if (!ALLOWED_AVATAR_STYLES.contains(style)) {
            throw new IllegalArgumentException("Estilo de avatar inválido");
        }
        
        String previewUrl = String.format("https://api.dicebear.com/8.x/%s/png?seed=%s", style, seed);
        return ResponseEntity.ok(previewUrl);
    }

    // Métodos auxiliares
    private String extractToken(String authHeader) {
        return authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
    }

    private void validateUserOwnership(String token, String userId) {
        String jwtToken = extractToken(token);
        User requestingUser = userService.getUserByToken(jwtToken);
        
        if (!requestingUser.getId().equals(userId)) {
            throw new SecurityException("Usuário não autorizado a modificar este recurso");
        }
    }
}