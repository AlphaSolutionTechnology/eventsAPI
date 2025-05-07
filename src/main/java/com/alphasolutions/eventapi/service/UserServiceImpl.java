package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.exception.UserAlreadyExistsException;
import com.alphasolutions.eventapi.exception.UserNotFoundException;
import com.alphasolutions.eventapi.model.dto.UserDTO;
import com.alphasolutions.eventapi.model.dto.UserData;
import com.alphasolutions.eventapi.model.entity.Evento;
import com.alphasolutions.eventapi.model.entity.Role;
import com.alphasolutions.eventapi.model.entity.User;
import com.alphasolutions.eventapi.repository.EventoRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
import com.alphasolutions.eventapi.utils.JwtUtil;
import com.alphasolutions.eventapi.utils.IdentifierGenerator;
import com.alphasolutions.eventapi.utils.PasswordUtils;
import com.google.api.client.json.webtoken.JsonWebToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordUtils passwordUtils;
    private final IdentifierGenerator identifierGenerator;
    private final RankingService rankingService;
    private final EventoRepository eventoRepository;

    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, RankingService rankingService, PasswordUtils passwordUtils, IdentifierGenerator identifierGenerator, EventoRepository eventoRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordUtils = passwordUtils;
        this.rankingService = rankingService;
        this.identifierGenerator = identifierGenerator;
        this.eventoRepository = eventoRepository;
    }

    @Override
    @Transactional
    public void createUser(UserDTO userDTO) {
        if (isEmailAlreadyExists(userDTO.getEmail())){
            throw new UserAlreadyExistsException("Já existe uma conta criada com este email!");
        }
        if (userDTO.getId() == null) {

            String id;
            String uniqueCode;

            do {
                id = IdentifierGenerator.generateIdentity(21);
            } while(userRepository.existsById(id));

            do {
                uniqueCode = identifierGenerator.generateUniqueCode();
            } while (userRepository.existsByUniqueCode(uniqueCode));

            userDTO.setId(id);
            userDTO.setUniqueCode(uniqueCode);
            userDTO.setPassword(passwordUtils.hashPassword(userDTO.getPassword()));
        }

        User userInDatabase = userRepository.findById(userDTO.getId()).orElse(null);


        if(userInDatabase == null) {
            String avatarUrl = "https://api.dicebear.com/7.x/adventurer/svg?seed=" + userDTO.getId();

            Role role = new Role(2L,"Participante");

            User userWithPassword = new User(
                userDTO.getId(),
                userDTO.getUsername(),
                role,
                null,
                userDTO.getEmail(),
                userDTO.getRedesocial(),
                userDTO.getPassword(),
                userDTO.getUniqueCode(),
                avatarUrl
            );

            userRepository.save(userWithPassword);
        }
    }

    @Override
    public User getUserById(String googleId) {
        return userRepository.findById(googleId).orElse(null);
    }

    @Override
    public User getUserByToken(String eventToken) {

        Map<String, Object> tokenData = jwtUtil.extractClaim(eventToken);
        User user = userRepository.findById(tokenData.get("id").toString()).orElse(null);

        if(user != null) {
            return user;
        }
        throw new UserNotFoundException("NO SUCH USER");
    }
    public boolean isEmailAlreadyExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User checkEmailAndPasswordValidityAndReturnUser(String email, String password) {

        User user = userRepository.findByEmail(email).orElse(null);

        if(user == null) {
            throw new UserNotFoundException("Usuário não encontrado!");
        }
        return passwordUtils.checkPassword(password, user.getPassword()) ? user:null;
    }


    public UserDTO prepareUserWithGoogleData(JsonWebToken.Payload googlePayload) {
        var email = googlePayload.get("email");
        var name = googlePayload.get("name");
        String uniqueCode;

        do {
            uniqueCode = identifierGenerator.generateUniqueCode();
        } while (userRepository.existsByUniqueCode(uniqueCode));

        return new UserDTO(
            googlePayload.getSubject(),
            (String) name,
            (String) email,
            null,
            uniqueCode,
            null
            
        );

    }
}
