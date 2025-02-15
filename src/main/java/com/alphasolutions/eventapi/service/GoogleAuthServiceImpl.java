package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.exception.UserAlreadyExistsException;
import com.alphasolutions.eventapi.model.Evento;
import com.alphasolutions.eventapi.model.Role;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.repository.UserRepository;
import com.alphasolutions.eventapi.utils.IdentifierGenerator;
import com.alphasolutions.eventapi.utils.JwtUtil;
import com.google.api.client.json.webtoken.JsonWebToken.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

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
        Payload googlePayload = jwtUtil.verifyGoogleToken(token);
        User user = userRepository.findById(googlePayload.getSubject()).orElse(null);
        if (user != null) {
            return jwtUtil.generateToken(user);
        }
        String uniqueCode;
        do {
            uniqueCode = IdentifierGenerator.generateIdentity(6);
        }while(userRepository.existsById(uniqueCode));

        user = userRepository.save(new User(googlePayload.getSubject(),(String) googlePayload.get("name"),new Role(2L,"Participante"),new Evento(1L,"Primeiro Evento"),(String) googlePayload.get("email"),null,uniqueCode));
        return jwtUtil.generateToken(user);
    }

}
