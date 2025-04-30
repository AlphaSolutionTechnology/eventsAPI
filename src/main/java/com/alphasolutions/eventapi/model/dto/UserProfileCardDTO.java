package com.alphasolutions.eventapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserProfileCardDTO {
    private String idUser;
    private String name;
    private String avatarUrl;
    private String bio;
    private List<ConexaoDTO> connections;
}
