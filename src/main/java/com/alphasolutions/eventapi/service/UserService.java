package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.model.UserDTO;

import java.util.List;

public interface UserService {



    User createUser(UserDTO userDTO) ;

    UserDTO getUserById(String id);

    User checkEmailAndPasswordValidityAndReturnUser(String email, String password);
}
