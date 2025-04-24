package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.exception.InvalidTokenException;
import com.alphasolutions.eventapi.exception.UserAlreadyExistsException;
import com.alphasolutions.eventapi.model.Evento;
import com.alphasolutions.eventapi.model.Role;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.repository.RankingRepository;
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
    private final RankingRepository rankingRepository;
    private final RankingService rankingService;

    public GoogleAuthServiceImpl(JwtUtil jwtUtil, UserRepository userRepository, RankingRepository rankingRepository, RankingService rankingService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.rankingRepository = rankingRepository;
        this.rankingService = rankingService;
    }

    @Override
    public String createAccountWithGoogle(String token) {
        Payload googlePayload;
        try{
            googlePayload = jwtUtil.verifyGoogleToken(token);
        } catch (Exception e) {
            throw new InvalidTokenException(e.getMessage());
        }

        // verifica se o usuario ja existe
        User user = userRepository.findById(googlePayload.getSubject()).orElse(null);
        if (user != null) {
            return jwtUtil.generateToken(user);
        }

        // Gera codigo unico
        String uniqueCode;
        do {
            uniqueCode = IdentifierGenerator.generateIdentity(6);
        } while(userRepository.existsById(uniqueCode));

        // Gera seed e estilo para o avatar
        String avatarSeed = googlePayload.getSubject() + "-" + System.currentTimeMillis();
        String avatarStyle = "adventurer"; // estilo padrao

        // Cria novo usuario com todos os campos necessarios
        user = new User(
            googlePayload.getSubject(),                  // id
            (String) googlePayload.get("name"),     // username
            new Role(2L, "Participante"),        // role
            new Evento(1L, "Primeiro Evento"),   // evento
            (String) googlePayload.get("email"),    // email
            null,                             // redeSocial
            null,                             // password
            uniqueCode,                                  // uniqueCode
            avatarSeed,                                  // avatarSeed    
            avatarStyle                                  // avatarStyle
        );

        userRepository.save(user);
        rankingService.inscreverUsuarioNoRanking(new Evento(1L,"Primeiro Evento"),user);
        
        return jwtUtil.generateToken(user);
    }

}
