package com.alphasolutions.eventapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserProfileUpdateDTO {

    // Dados pessoais
    private String username;
    private String email;

    // Avatar e bio e badges
    private String avatarStyle;
    private String avatarSeed;
    private String avatarUrl;

    private String badges;
    
    private String bio;
    
}
