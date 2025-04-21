package com.alphasolutions.eventapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor  // Cria construtur vazio
@AllArgsConstructor // Cria construtur com todos os campos
@Data               // Gera getters, setter, toString, equals
public class AvatarUpdateDTO {
    private String avatarSeed;
    private String avatarStyle;

    
}
