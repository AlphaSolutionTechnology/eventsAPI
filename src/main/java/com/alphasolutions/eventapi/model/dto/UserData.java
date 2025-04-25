package com.alphasolutions.eventapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserData {
    private String idUser;
    private String name;
    private String email;
    private String role;
    private String uniqueCode;
    private String avatarSeed;

    public UserData(String idUser,String nome, String email, String role, String uniqueCode) {
        this.idUser = idUser;
        this.name = nome;
        this.email = email;
        this.role = role;
        this.uniqueCode = uniqueCode;

    }
}
