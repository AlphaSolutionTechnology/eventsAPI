package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.exception.UserAlreadyExistsException;
import com.alphasolutions.eventapi.exception.UserNotFoundException;
import com.alphasolutions.eventapi.model.*;
import com.alphasolutions.eventapi.repository.UserRepository;
import com.alphasolutions.eventapi.utils.JwtUtil;
import com.alphasolutions.eventapi.utils.IdentifierGenerator;
import com.alphasolutions.eventapi.utils.PasswordUtils;
import com.google.api.client.json.webtoken.JsonWebToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordUtils passwordUtils;
    private final IdentifierGenerator identifierGenerator;
    private final RankingService rankingService;


    // Listando os estilos permitidos
    private final List<String> ALLOWED_AVATAR_STYLES = List.of(
        "adventurer",
        "big-ears",
        "botts",
        "pixel-art"
    );

    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, RankingService rankingService,PasswordUtils passwordUtils, IdentifierGenerator identifierGenerator) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordUtils = passwordUtils;
        this.rankingService = rankingService;
        this.identifierGenerator = identifierGenerator;

    }

    @Override
    @Transactional
    public void createUser(UserDTO userDTO) {
        if(isEmailAlreadyExists(userDTO.getEmail())){
            throw new UserAlreadyExistsException("Já existe uma conta criada com este email!");
        }

        if(userDTO.getId() == null) {
            String id;
            String uniqueCode;

            do {
                id = IdentifierGenerator.generateIdentity(21);
            } while(userRepository.existsById(id));

            do {
                uniqueCode = identifierGenerator.generateUniqueCode();
            } while (userRepository.existsByUniqueCode(uniqueCode));

            // Se for novo usuario, gerar seed se nao existir
            if (userDTO.getAvatarSeed() == null) {
                userDTO.setAvatarSeed(userDTO.getEmail() + "-" + System.currentTimeMillis());
            }

            // Definir estilo padrao se nao existir
            if (userDTO.getAvatarStyle() == null) {
                userDTO.setAvatarStyle("adventurer");
            }

            System.out.println("Avatar Seed: " + userDTO.getAvatarSeed());
            System.out.println("Avatar Style: " + userDTO.getAvatarStyle());
            
            userDTO.setId(id);
            userDTO.setUniqueCode(uniqueCode);
            userDTO.setPassword(passwordUtils.hashPassword(userDTO.getPassword()));
        }

        User userInDatabase = userRepository.findById(userDTO.getId()).orElse(null);

        if(userInDatabase == null) {

            Evento evento = new Evento(1L,"Primeiro Evento");
            Role role = new Role(2L,"Participante");

            User userWithPassword = new User(
                userDTO.getId(),
                userDTO.getUsername(),
                role,
                evento,
                userDTO.getEmail(),
                userDTO.getRedeSocial(),
                userDTO.getPassword(),
                userDTO.getUniqueCode(),
                userDTO.getAvatarSeed(),
                userDTO.getAvatarStyle(),
                null // gerado automaticamente
            );

            userRepository.save(userWithPassword);
            rankingService.inscreverUsuarioNoRanking(evento, userWithPassword);
        }
    }

    @Override
    public User updateUserAvatarStyle(String userId, String newStyle) {

        if (!ALLOWED_AVATAR_STYLES.contains(newStyle.toLowerCase())) {
            throw new IllegalArgumentException("Estilo de avatar nao permitido");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("Usuario nao encontrado"));

        user.setAvatarStyle(newStyle);
        return userRepository.save(user);
    }

    @Override
    public List<String> getAvailableAvatarStyles() {
        return List.of("adventurer", "big-ears", "botts", "pixel-art");
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
        User user = userRepository.findByEmailWithAvatar(email).orElse(null);

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

        // Gerar um seed unico para o avatar ( usando email+ timestamp )
        String avatarSeed = (String) email + "-" + System.currentTimeMillis();

        // Definir um estilo padrao inicial ( Pode ser aleatorio ou fixo )
        String avatarStyle = "adventurer"; // Estilo padrao inicial

        return new UserDTO(
            googlePayload.getSubject(),
            (String) name,
            (String) email,
            null,
            uniqueCode,
            null,
            avatarSeed,
            avatarStyle,
            null
        );

    }
}
