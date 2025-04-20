package com.alphasolutions.eventapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.time.Instant;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Omite campos nulos
public class UserResponseDTO {
    private String id;
    private String name;
    private String email;
    private String role;
    private String uniqueCode;
    private String redeSocial;
    private String avatarSeed;
    private String avatarStyle;
    private String avatarUrl;
    private Instant createdAt;
    private Boolean isEmailVerified;
    
    // Relacionamentos simplificados
    private String eventoId;
    private String palestraId;

    // Construtor que aceita User
    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.name = user.getNome();
        this.email = user.getEmail();
        this.role = user.getRole() != null ? user.getRole().getRole() : null;
        this.uniqueCode = user.getUniqueCode();
        this.redeSocial = user.getRedeSocial();
        this.avatarSeed = user.getAvatarSeed();
        this.avatarStyle = user.getAvatarStyle();
        this.avatarUrl = user.getAvatarUrl();
        this.createdAt = user.getCreatedAt();
        this.isEmailVerified = user.getIsEmailVerified();
        this.eventoId = user.getEvento() != null ? String.valueOf(user.getEvento().getId()) : null;
        this.palestraId = user.getPalestra() != null ? String.valueOf(user.getPalestra().getId()) : null;
    }
}