package com.alphasolutions.eventapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserData {
    private String name;
    private String email;
    private String role;
    private String uniqueCode;
    private String avatarSeed;
}
