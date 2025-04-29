package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.exception.InvalidTokenException;
import com.alphasolutions.eventapi.model.entity.Evento;
import com.alphasolutions.eventapi.model.entity.Role;
import com.alphasolutions.eventapi.model.entity.User;
import com.alphasolutions.eventapi.repository.UserRepository;
import com.alphasolutions.eventapi.utils.IdentifierGenerator;
import com.alphasolutions.eventapi.utils.JwtUtil;
import com.google.api.client.json.webtoken.JsonWebToken.Payload;
import org.springframework.stereotype.Service;

@Service
public class GoogleAuthServiceImpl implements GoogleAuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public GoogleAuthServiceImpl(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;

    }

    @Override
    public String createAccountWithGoogle(String token) {
        Payload googlePayload;

        try{
            googlePayload = jwtUtil.verifyGoogleToken(token);
        } catch (Exception e) {
            throw new InvalidTokenException(e.getMessage());
        }

        User user = userRepository.findById(googlePayload.getSubject()).orElse(null);

        if (user != null) {
            return jwtUtil.generateToken(user);
        }

        String uniqueCode;

        do {
            uniqueCode = IdentifierGenerator.generateIdentity(6);
        } while(userRepository.existsById(uniqueCode));

        // Gerando URL do avatar usando o ID do google
        String avatarUrl = "https://api.dicebear.com/8.x/adventurers/svg?seed=" + googlePayload.getSubject();

        user = userRepository.save(new User(
            googlePayload.getSubject(),
            (String) googlePayload.get("name"),
            new Role(2L,"Participante"),
            new Evento(1L,"Primeiro Evento"),
            (String) googlePayload.get("email"),
            null,
            uniqueCode,
            avatarUrl
        ));

        return jwtUtil.generateToken(user);
    }

}
