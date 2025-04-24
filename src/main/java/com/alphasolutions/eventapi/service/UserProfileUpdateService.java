package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.UserProfileUpdateDTO;
import com.alphasolutions.eventapi.model.UserDTO;

public interface UserProfileUpdateService {

    void updateAvatar(UserProfileUpdateDTO avatarStyle, UserProfileUpdateDTO avatarSeed, UserProfileUpdateDTO avatarUrl);
    UserProfileUpdateDTO updateAvatarUrl(String avatarUrl);

    UserProfileUpdateDTO updateName(UserDTO username);
    UserProfileUpdateDTO updateEmail(UserDTO email);

    UserProfileUpdateDTO updateBio(UserProfileUpdateDTO bio);
    UserProfileUpdateDTO updateBadges(UserProfileUpdateDTO badges);

}
