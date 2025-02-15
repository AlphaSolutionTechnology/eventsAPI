package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.model.UserDTO;

public interface UserService {

    void createUser(UserDTO userDTO) ;

    User getUserById(String googleId);

    User checkEmailAndPasswordValidityAndReturnUser(String email, String password);
}
