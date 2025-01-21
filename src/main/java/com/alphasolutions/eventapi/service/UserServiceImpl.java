package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.Evento;
import com.alphasolutions.eventapi.model.Role;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.model.UserDTO;
import com.alphasolutions.eventapi.repository.EventoRepository;
import com.alphasolutions.eventapi.repository.RoleRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
import com.alphasolutions.eventapi.utils.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final EventoRepository eventoRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, EventoRepository eventoRepository, RoleRepository roleRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.eventoRepository = eventoRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public User createUser(UserDTO userDTO) {

        if(userRepository.findById(userDTO.getId()).isEmpty()) {
            Evento evento = eventoRepository.findById(1).orElse(null);
            Role role = roleRepository.findById(2L).orElse(null);
            User user = new User(userDTO.getId(),userDTO.getUsername(),userDTO.getEmail(),role,evento,userDTO.getRedesocial());
            return userRepository.save(user);
        }
        return userRepository.findById(userDTO.getId()).orElse(null);
    }

    @Override
    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return new UserDTO(user.getId(),user.getNome(), user.getEmail(), user.getRedeSocial());
    }

    public User getUserByUniqueId(Long uniqueId) {
        return userRepository.findByUniqueCode(uniqueId);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> new UserDTO(user.getId(),user.getNome(), user.getEmail(), user.getRedeSocial())).toList();
    }

    @Override
    public boolean signIn(String token) {
        return false;
    }
}
