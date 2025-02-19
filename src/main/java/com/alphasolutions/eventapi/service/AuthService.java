package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.User;

public interface AuthService {
    String authenticate(String email, String password);
    String auhenticate(String token);
    String authenticateAdmin(String token);
}
