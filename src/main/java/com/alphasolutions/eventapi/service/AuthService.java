package com.alphasolutions.eventapi.service;

import java.util.Map;

public interface AuthService {
    String authenticate(String email, String password);
    void authenticate(String token);
    void authenticateAdmin(String token);
    Map<String, Object> authenticateWithDetails(String token);
}
