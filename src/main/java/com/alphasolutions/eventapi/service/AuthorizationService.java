package com.alphasolutions.eventapi.service;


public interface AuthorizationService {
    boolean isRoleAdmin(String token);
    boolean isUserSolicitant(String token, String uniqueCode );
}
