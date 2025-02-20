package com.alphasolutions.eventapi.service;


import java.util.Optional;

import com.alphasolutions.eventapi.exception.PalestraNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.repository.PalestraRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
import com.alphasolutions.eventapi.utils.IdentifierGenerator;

@Service
public class PalestraService {
    

    private final PalestraRepository palestraRepository;
    private final RankingService rankingService;
    private final UserRepository userRepository;
    private final UserService userService;

    public PalestraService(PalestraRepository palestraRepository, RankingService rankingService, UserRepository userRepository, UserService userService){
        this.palestraRepository = palestraRepository;
        this.rankingService = rankingService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

     public Palestra criarPalestra(Palestra palestra) {
        String uniqueCode;
        do {
            uniqueCode = IdentifierGenerator.generateIdentity(5);
        } while (palestraRepository.existsByUniqueCode(uniqueCode));

        palestra.setUniqueCode(uniqueCode);

        return palestraRepository.save(palestra);
    }


    public void inscreverUsuarioNaPalestra(Long id, User user){
        Optional<Palestra> palestra = palestraRepository.findById(id);
        user.setPalestra(palestra.get());
        userRepository.save(user);

    }

    public boolean isUsuarioInscritoNaPalestra(Palestra palestra, User user){
        if(palestra.getId().equals(user.getPalestra().getId())){
            return true;
        }
        return false;
    }

    public void desinscreverUsuarioDaPalestra(User user) {
        user.setPalestra(null);
        userRepository.save(user);
    }

    @Transactional
    public void deletePalestra(Long id) {
        if(palestraRepository.existsById(id)) {
            palestraRepository.removePalestraById(id);
            return;
        }
        throw new PalestraNotFoundException("No such palestra");
    }

    public Palestra findPalestra(String uniqueCode) {
        Palestra palestra = palestraRepository.findByUniqueCode(uniqueCode).orElse(null);
        if(palestra == null) {
            throw new PalestraNotFoundException("No such palestra");
        }
        return palestra;
    }

    public Palestra findPalestraById(Long id) {
        Palestra palestra = palestraRepository.findById(id).orElse(null);
        if(palestra == null) {
            throw new PalestraNotFoundException("No such palestra");
        }
        return palestra;
    }
}
