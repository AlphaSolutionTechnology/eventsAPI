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
import org.springframework.http.MediaType;
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
            // 1. Autenticação do token com o AuthService
            authService.authenticate(eventToken);

            // 2. Busca o usuário
            User user = userService.getUserByToken(eventToken);
            
            // 3. Converte User para DTO (protege dados sensíveis)
            UserProfileCardDTO dto = UserMapper.toProfileCardDTO(user);

            // 4. Retorna o DTO
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        } 
    }
}
