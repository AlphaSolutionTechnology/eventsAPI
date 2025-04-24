package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.exception.UserNotFoundException;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.model.UserResponseDTO;
import com.alphasolutions.eventapi.repository.UserRepository;
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
import com.alphasolutions.eventapi.model.UserDTO;

import com.alphasolutions.eventapi.model.UserUpdateDTO;
import com.alphasolutions.eventapi.model.AvatarUpdateDTO;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    //Injecao de dependencia
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // Buscar perfil do usuario autenticado
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(
        @CookieValue("eventToken") String token
    ) {
        System.out.println("TOKEN RECEBIDO: " + token);
        // Extrai o ID do usuário do token JWT
        String userId = jwtUtil.extractClaim(token).get("id").toString();
        
        // Busca o usuário no banco
        User user = userService.findById(userId);
        
        // Converte para DTO de resposta
        UserResponseDTO response = new UserResponseDTO(user);
        
        return ResponseEntity.ok(response);
    }

    // Atualizar avatar
    @PatchMapping("/avatar")
    public ResponseEntity<UserResponseDTO> updateAvatar(
        @RequestBody AvatarUpdateDTO updateDTO,
        @CookieValue("eventToken") String token
    ) {
        // Validação do token e extração do ID
        String userId = jwtUtil.extractClaim(token).get("id").toString();
        
        // Atualiza no banco
        User updatedUser = userService.updateAvatar(
            userId,
            updateDTO.getAvatarStyle(),
            updateDTO.getAvatarSeed()
        );
        
        // Retorna o usuário atualizado
        return ResponseEntity.ok(new UserResponseDTO(updatedUser));
    }

    // Atualizar outros dados do usuário
    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateProfile(
        @RequestBody UserUpdateDTO updateDTO,
        @CookieValue("eventToken") String token
    ) {
        String userId = jwtUtil.extractClaim(token).get("id").toString();
        
        User updatedUser = userService.updateUser(userId, updateDTO);

        return ResponseEntity.ok(new UserResponseDTO(updatedUser));
    }

    
    
}