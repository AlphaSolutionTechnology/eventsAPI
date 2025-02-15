package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.exception.UserNotFoundException;
import com.alphasolutions.eventapi.model.*;
import com.alphasolutions.eventapi.repository.EventoRepository;
import com.alphasolutions.eventapi.repository.RankingRepository;
import com.alphasolutions.eventapi.repository.RoleRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
import com.alphasolutions.eventapi.utils.JwtUtil;
import com.alphasolutions.eventapi.utils.IdentifierGenerator;
import com.alphasolutions.eventapi.utils.PasswordUtils;
import com.google.api.client.json.webtoken.JsonWebToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RankingRepository rankingRepository;
    private final PasswordUtils passwordUtils;
    private final IdentifierGenerator identifierGenerator;

    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, RankingRepository rankingRepository, PasswordUtils passwordUtils, IdentifierGenerator identifierGenerator) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.rankingRepository = rankingRepository;
        this.passwordUtils = passwordUtils;
        this.identifierGenerator = identifierGenerator;
    }

    @Override
    @Transactional
    public User createUser(UserDTO userDTO) {
        if(userDTO.getId() == null) {
            String id;
            String uniqueCode;
            do {
                id = IdentifierGenerator.generateIdentity(21);
            }while(userRepository.existsById(id));
            do {
                uniqueCode = identifierGenerator.generateUniqueCode();
            }while (userRepository.existsByUniqueCode(uniqueCode));
            userDTO.setId(id);
            userDTO.setUniqueCode(uniqueCode);
            userDTO.setPassword(passwordUtils.hashPassword(userDTO.getPassword()));
        }
        User userInDatabase = userRepository.findById(userDTO.getId()).orElse(null);
        if(userInDatabase == null) {
            Evento evento = new Evento(1L,"Primeiro Evento");
            Role role = new Role(2L,"Participante");
            User user = new User(userDTO.getId(),userDTO.getUsername(),role,evento,userDTO.getEmail(),userDTO.getRedesocial(), userDTO.getUniqueCode());
            if(userDTO.getPassword() == null) {
                userRepository.save(user);
                rankingRepository.save(new Ranking(evento,user));
            }else{
                User userWithPassword = new User(userDTO.getId(),userDTO.getUsername(),role,evento,userDTO.getEmail(),userDTO.getRedesocial(), userDTO.getPassword() ,userDTO.getUniqueCode());
                userRepository.save(userWithPassword);
                rankingRepository.save(new Ranking(evento,userWithPassword));
            }
            return user;
        }
        return userRepository.findById(userDTO.getId()).orElse(null);

    }

    @Override
    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return new UserDTO(user.getId(),user.getNome(), user.getEmail(),null, user.getUniqueCode(), user.getRedeSocial());
    }

    public String giveUserAnotherToken(User user){
        return jwtUtil.generateToken(user);
    }

    public User retrieveUserById(String googleId) {
        return userRepository.findById(googleId).orElse(null);
    }

    public boolean isEmailAlreadyExists(String email) {
        return userRepository.existsByEmail(email);
    }

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
        }while (userRepository.existsByUniqueCode(uniqueCode));
        return new UserDTO(googlePayload.getSubject(), (String) name, (String) email,null, uniqueCode ,null);

    }
}
