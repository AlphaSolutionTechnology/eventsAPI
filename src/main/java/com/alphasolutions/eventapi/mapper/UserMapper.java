package com.alphasolutions.eventapi.mapper;

import com.alphasolutions.eventapi.model.dto.UserProfileCardDTO;
import com.alphasolutions.eventapi.model.entity.User;


public interface UserMapper {
    public static UserProfileCardDTO toProfileCardDTO(User user) {
        if (user == null) {
            return null;
        }

        UserProfileCardDTO dto = new UserProfileCardDTO();
        dto.setIdUser(user.getIdUser());
        dto.setName(user.getNome());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBio(user.getBio());
        

        return dto;
    }
}
