package com.alphasolutions.eventapi.service;


import org.springframework.stereotype.Service;
import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.repository.PalestraRepository;
import com.alphasolutions.eventapi.utils.UniqueCodeUtil;

@Service
public class PalestraService {
    

    private final PalestraRepository palestraRepository;

    public PalestraService(PalestraRepository palestraRepository){
        this.palestraRepository = palestraRepository;
    }

    
     // Método para gerar código único e salvar palestra
     public Palestra criarPalestra(Palestra palestra) {
        
        String uniqueCode;

        do {
            uniqueCode = UniqueCodeUtil.generateUniqueCode(6); 
        } while (palestraRepository.existsByUniqueCode(uniqueCode)); // Verifica se o código já existe

        palestra.setUniqueCode(uniqueCode);

        return palestraRepository.save(palestra);
    }

    
    public boolean verificarPalestra(String uniqueCode){
        return palestraRepository.existsByUniqueCode(uniqueCode);
    }

}
