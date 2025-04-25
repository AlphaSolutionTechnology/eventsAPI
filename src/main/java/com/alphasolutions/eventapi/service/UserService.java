package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.entity.User;
import com.alphasolutions.eventapi.model.dto.UserDTO;

public interface UserService {

    void createUser(UserDTO userDTO) ;

    User getUserById(String googleId);

    User getUserByToken(String token);

    User checkEmailAndPasswordValidityAndReturnUser(String email, String password);
}
