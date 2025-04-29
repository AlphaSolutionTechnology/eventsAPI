package com.alphasolutions.eventapi.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alphasolutions.eventapi.exception.UserNotFoundException;
import com.alphasolutions.eventapi.model.dto.UserProfileCardDTO;
import com.alphasolutions.eventapi.service.AuthService;
import com.alphasolutions.eventapi.service.UserService;
import com.alphasolutions.eventapi.model.entity.User;
import com.alphasolutions.eventapi.mapper.UserMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    public UserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    // Dados completos do usuario
    @GetMapping("/profile/me")
    public ResponseEntity<UserProfileCardDTO> getUserProfile(
        @CookieValue(value = "eventToken", required = false) String eventToken
    ) {
        try {
            // 1. Validação do token
            if (eventToken == null || eventToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
            }

            // 2. Autenticação
            authService.authenticate(eventToken); // Lança AuthenticationException se falhar

            // 3. Busca o usuário
            User user = userService.getUserByToken(eventToken);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
            }

            // 4. Converte User para DTO (protege dados sensíveis)
            UserProfileCardDTO dto = UserMapper.toProfileCardDTO(user);

            // 5. Retorna o DTO
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        } 
    }
}
