package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.*;
import com.alphasolutions.eventapi.repository.EventoRepository;
import com.alphasolutions.eventapi.repository.RankingRepository;
import com.alphasolutions.eventapi.repository.RoleRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
import com.alphasolutions.eventapi.utils.JwtUtil;
import com.alphasolutions.eventapi.utils.UniqueCodeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EventoRepository eventoRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final RankingRepository rankingRepository;

    public UserServiceImpl(UserRepository userRepository, EventoRepository eventoRepository, RoleRepository roleRepository, JwtUtil jwtUtil, RankingRepository rankingRepository) {
        this.userRepository = userRepository;
        this.eventoRepository = eventoRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
        this.rankingRepository = rankingRepository;
    }

    @Override
    @Transactional
    public User createUser(UserDTO userDTO) {
        if(userRepository.findById(userDTO.getId()).isEmpty()) {
            Evento evento = eventoRepository.findById(1).orElse(null);
            Role role = roleRepository.findById(2L).orElse(null);
            User user = new User(userDTO.getId(),userDTO.getUsername(),role,evento,userDTO.getEmail(),userDTO.getRedesocial(), userDTO.getUniqueCode());
            userRepository.save(user);
            // rankingRepository.save(new Ranking(eventoRepository.findById(1).orElse(null),user));
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
        return new UserDTO(user.getId(),user.getNome(), user.getEmail(), user.getUniqueCode(), user.getRedeSocial());
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> new UserDTO(user.getId(),user.getNome(), user.getEmail(), user.getUniqueCode(),user.getRedeSocial())).toList();
    }

    @Override
    public boolean signIn(String token) {
        return false;
    }

    public String giveUserAnotherToken(User user){
        return jwtUtil.generateToken(user);
    }
    public User userExists(String googleId) {
        return userRepository.findById(googleId).orElse(null);
    }
    public String generateUniqueCode(){
        String uniqueCode;
        User user;
        do{
            uniqueCode = UniqueCodeUtil.generateUniqueCode(6);
            user = userRepository.findByUniqueCode(uniqueCode);
        }while (user != null);
        return uniqueCode;
    }

}
