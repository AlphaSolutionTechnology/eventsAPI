package com.alphasolutions.eventapi.service;


import java.util.Optional;

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

    public PalestraService(PalestraRepository palestraRepository, RankingService rankingService, UserRepository userRepository){
        this.palestraRepository = palestraRepository;
        this.rankingService = rankingService;
        this.userRepository = userRepository;
    }

    
     // Método para gerar código único e salvar palestra
     public Palestra criarPalestra(Palestra palestra) {
        
        String uniqueCode;

        do {
            uniqueCode = IdentifierGenerator.generateIdentity(6);
        } while (palestraRepository.existsByUniqueCode(uniqueCode)); // Verifica se o código já existe

        palestra.setUniqueCode(uniqueCode);

        return palestraRepository.save(palestra);
    }

    
    public boolean verificarPalestra(String uniqueCode){
        return palestraRepository.existsByUniqueCode(uniqueCode);
    }

    public User inscreverUsuarioNaPalestra(Long id, User user){
        
        Optional<Palestra> palestra = palestraRepository.findById(id);

            user.setPalestra(palestra.get());
            
            return userRepository.save(user);

    }

    public boolean isUsuarioInscritoNaPalestra(Optional<Palestra> palestra, Optional<User> user){

        boolean isInscrito = userRepository.existsByPalestra(palestra);

        return isInscrito;
    }

    public User desinscreverUsuarioDaPalestra(Palestra palestra, User user) {       
        // rankingService.removerUsuarioDoRanking(palestra, user);

        user.setPalestra(null);

        return userRepository.save(user);

    }

    

}
