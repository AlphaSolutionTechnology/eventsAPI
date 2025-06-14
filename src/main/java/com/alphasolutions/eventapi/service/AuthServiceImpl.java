package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.exception.InvalidRoleException;
import com.alphasolutions.eventapi.exception.InvalidTokenException;
import com.alphasolutions.eventapi.exception.UserNotFoundException;
import com.alphasolutions.eventapi.model.entity.User;
import com.alphasolutions.eventapi.repository.UserRepository;
import com.alphasolutions.eventapi.utils.JwtUtil;
import com.alphasolutions.eventapi.utils.PasswordUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {
    private final PasswordUtils passwordUtils;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthorizationService authorizationService;

    public AuthServiceImpl(PasswordUtils passwordUtils, UserRepository userRepository, JwtUtil jwtUtil, AuthorizationService authorizationService) {
        this.passwordUtils = passwordUtils;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authorizationService = authorizationService;
    }

    @Override
    public String authenticate(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);
        if(user == null) {
            throw new UserNotFoundException("Invalid credentials");
        }
        boolean isValid = passwordUtils.checkPassword(password, user.getPassword());
        if(!isValid) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return jwtUtil.generateToken(user);
    }

    @Override
    public void authenticate(String token) {
        Map<String,Object> claim =  jwtUtil.extractClaim(token);
        if(claim.get("error") == null){
            return;
        }
        throw new InvalidTokenException("Invalid token");
    }

    @Override
    public void authenticateAdmin(String token) {
        boolean isUserAdmin = authorizationService.isRoleAdmin(token);
        if (!isUserAdmin) {
            throw new InvalidRoleException("Invalid role");
        }
    }

}
