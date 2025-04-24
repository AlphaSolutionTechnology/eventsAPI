package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.exception.InvalidRoleException;
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
    private final AuthorizationService authorizationService;

    public AuthServiceImpl(PasswordUtils passwordUtils, UserRepository userRepository, JwtUtil jwtUtil, AuthorizationService authorizationService) {
        this.passwordUtils = passwordUtils;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authorizationService = authorizationService;
    }

    @Override
    public String authenticate(String email, String password) {
        User user = userRepository.findByEmailWithAvatar(email)
            .orElseThrow(() -> new UserNotFoundException("Credenciais Inválidas"));

        if(user == null) {
            throw new UserNotFoundException("Credenciais Inválidas");
        }
        boolean isValid = passwordUtils.checkPassword(password, user.getPassword());
        if(!isValid) {
            throw new IllegalArgumentException("Credenciais Inválidas");
        }

        // Garante que o usuario tem avatar
        if (user.getAvatarSeed() == null || user.getAvatarStyle() == null) {
            user.setAvatarSeed(email + "-" + System.currentTimeMillis());
            user.setAvatarStyle("adventurer"); // estilo padrao
            userRepository.save(user);
            System.out.println("Avatar padrao definido para:" + email);
        }
        return jwtUtil.generateToken(user);
    }

    @Override
    public Map<String, Object> authenticateWithDetails(String token) {
        Map<String, Object> claims = jwtUtil.extractClaim(token);
        if (claims.get("error") != null) {
            throw new InvalidTokenException("Token inválido");
        }
        
        // Completa com dados do usuário se necessário
        User user = userRepository.findById(claims.get("id").toString())
            .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        
        claims.put("avatarUrl", buildAvatarUrl(user));
        return claims;
    }
    
    private String buildAvatarUrl(User user) {
        return "https://api.dicebear.com/8.x/" + 
               user.getAvatarStyle() + 
               "/svg?seed=" + 
               user.getAvatarSeed();
    }

    @Override
    public void authenticate(String token) {
        Map<String,Object> claim =  jwtUtil.extractClaim(token);
        if(claim.get("error") == null){
            return;
        }
        throw new InvalidTokenException("Token invalido");
    }

    @Override
    public void authenticateAdmin(String token) {
        boolean isUserAdmin = authorizationService.isRoleAdmin(token);
        if (!isUserAdmin) {
            throw new InvalidRoleException("Invalid role");
        }
    }

}
