package com.alphasolutions.eventapi.service;

public interface AuthService {
    String authenticate(String email, String password);
    void authenticate(String token);
    void authenticateAdmin(String token);
}
