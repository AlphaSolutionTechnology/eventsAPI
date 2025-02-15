package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.exception.InvalidTokenException;
import com.alphasolutions.eventapi.exception.UserNotFoundException;
import com.alphasolutions.eventapi.model.User;
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

    public AuthServiceImpl(PasswordUtils passwordUtils, UserRepository userRepository, JwtUtil jwtUtil) {
        this.passwordUtils = passwordUtils;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String authenticate(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);
        if(user == null) {
            throw new UserNotFoundException("Credenciais Inválidas");
        }
        boolean isValid = passwordUtils.checkPassword(password, user.getPassword());
        if(!isValid) {
            throw new IllegalArgumentException("Credenciais Inválidas");
        }
        return jwtUtil.generateToken(user);
    }

    @Override
    public String auhenticate(String token) {
        Map<String,Object> claim =  jwtUtil.extractClaim(token);
        if(claim.get("error") == null){
            return token;
        }
        throw new InvalidTokenException("Token invalido");
    }
}
