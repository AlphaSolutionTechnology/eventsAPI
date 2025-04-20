package com.alphasolutions.eventapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor  // Cria construtur vazio
@AllArgsConstructor // Cria construtur com todos os campos
@Data               // Gera getters, setter, toString, equals
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private String password;
    private String uniqueCode;
    private String redeSocial;
    
    // Novos campos do avatar
    private String avatarSeed;
    private String avatarStyle;
    private String avatarUrl; // URL do avatar gerada dinamicamente
}
