package com.alphasolutions.eventapi.service;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.alphasolutions.eventapi.exception.PalestraNotFoundException;
import com.alphasolutions.eventapi.model.dto.PalestraDTO;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import com.alphasolutions.eventapi.model.entity.Palestra;
import com.alphasolutions.eventapi.model.entity.User;
import com.alphasolutions.eventapi.repository.PalestraRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
import com.alphasolutions.eventapi.utils.IdentifierGenerator;

@Service
public class PalestraService {
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
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
        user.setPalestraAtual(palestra);
        userRepository.save(user);

    }

    public boolean isUsuarioInscritoNaPalestra(Palestra palestra, User user){
        Palestra palestraInUserTable = user.getPalestraAtual();
        if(palestraInUserTable == null){
            return false;
        }
        if(palestra == null){
            throw new PalestraNotFoundException("Palestra nao encontrada");
        }
        return palestra.getIdPalestra().equals(palestraInUserTable.getIdPalestra());
    }

    public void desinscreverUsuarioDaPalestra(User user) {
        user.setPalestraAtual(null);
        userRepository.save(user);
    }

    @Transactional
    public void deletePalestra(Long id) {
        if(palestraRepository.existsById(id)) {
            palestraRepository.removePalestraByIdPalestra(id);
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

    public List<PalestraDTO> findAllPalestras() {
        List<PalestraDTO> palestraList = new ArrayList<>();

        for(Palestra palestra : palestraRepository.findAll()) {
            palestraList.add(new
                    PalestraDTO(
                        palestra.getIdPalestra(),
                        palestra.getTema(),
                        palestra.getPalestrante(),
                        palestra.getHoraLiberacao(),
                        palestra.getQuizzLiberado(),
                        palestra.getUniqueCode(),
                        palestra.getDescricao()
                    )
            );
        }
        return palestraList;
    }

    public List<PalestraDTO> findAllUserPalestra(User user) {
        List<Palestra> palestras =  palestraRepository.findAllByUser(user);
        List<PalestraDTO> palestrasDTO = new ArrayList<>(palestras.size());
        for (Palestra palestra : palestras) {
            palestrasDTO.add(new PalestraDTO(palestra.getIdPalestra(), palestra.getTema(), palestra.getPalestrante(),palestra.getHoraLiberacao(),palestra.getQuizzLiberado(), palestra.getUniqueCode(),palestra.getDescricao()));
        }
        return palestrasDTO;
    }

    @Transactional
    public void unsubscribeAllUsersFromPalestra(Long id) {
        if (palestraRepository.existsById(id)) {
            userRepository.unsubscribeUsersFromPalestraAtual(id);
        }
    }

    
    }






