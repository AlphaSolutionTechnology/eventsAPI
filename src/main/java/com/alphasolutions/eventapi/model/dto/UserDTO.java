package com.alphasolutions.eventapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private String password;
    private String uniqueCode;
    private String redesocial;
}
