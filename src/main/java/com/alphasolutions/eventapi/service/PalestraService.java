package com.alphasolutions.eventapi.service;


import java.util.List;

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

     public Palestra criarPalestra(Palestra palestra, String eventToken) {
        String uniqueCode;
        User user = userService.getUserByToken(eventToken);
        do {
            uniqueCode = IdentifierGenerator.generateIdentity(5);
        } while (palestraRepository.existsByUniqueCode(uniqueCode));
        palestra.setUser(user);
        palestra.setUniqueCode(uniqueCode);
        return palestraRepository.save(palestra);
    }


    public void inscreverUsuarioNaPalestra(Palestra palestra, User user){
        user.setPalestra(palestra);
        userRepository.save(user);

    }

    public boolean isUsuarioInscritoNaPalestra(Palestra palestra, User user){
        Palestra palestraInUserTable = user.getPalestra();
        if(palestraInUserTable == null){
            return false;
        }
        if(palestra == null){
            throw new PalestraNotFoundException("Palestra nao encontrada");
        }
        return palestra.getId().equals(palestraInUserTable.getId());
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

    public List<Palestra> findAllUserPalestra(User user) {
        return palestraRepository.findAllByUser(user);
    }

    @Transactional
    public void unsubscribeAllUsersFromPalestra(Long id) {
        if (palestraRepository.existsById(id)) {
            userRepository.unsubscribeUsersFromPalestra(id);
        }
    }
}
