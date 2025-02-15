package com.alphasolutions.eventapi.service;


import org.springframework.stereotype.Service;
import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.repository.PalestraRepository;
import com.alphasolutions.eventapi.utils.IdentifierGenerator;

@Service
public class PalestraService {
    

    private final PalestraRepository palestraRepository;
    private final RankingService rankingService;

    public PalestraService(PalestraRepository palestraRepository, RankingService rankingService){
        this.palestraRepository = palestraRepository;
        this.rankingService = rankingService;
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

    public void desinscreverUsuarioDaPalestra(Palestra palestra, User user) {       
        rankingService.removerUsuarioDoRanking(palestra, user);
    }

    

}
