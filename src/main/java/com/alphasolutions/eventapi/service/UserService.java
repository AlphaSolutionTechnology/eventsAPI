package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.model.UserDTO;
import com.alphasolutions.eventapi.model.UserUpdateDTO;

import java.util.List;

public interface UserService {

    void createUser(UserDTO userDTO) ;

    User getUserById(String googleId);

    User getUserByToken(String token);

    User checkEmailAndPasswordValidityAndReturnUser(String email, String password);

    User updateUserAvatarStyle(String userId, String newStyle);

    List<String> getAvailableAvatarStyles();

    User findById(String userId);

    User updateUser(String userId, UserUpdateDTO updateDTO);

    User updateAvatar(String userId, String avatarStyle, String avatarSeed);
}
