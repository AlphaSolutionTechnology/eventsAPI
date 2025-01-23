package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.Questoes;
import com.alphasolutions.eventapi.repository.QuestoesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestoesService {

    @Autowired
    private QuestoesRepository questoesRepository;

    public List<Questoes> findAll() {
        return questoesRepository.findAll();
    }

    public Optional<Questoes> findById(Long id) {
        return questoesRepository.findById(id);
    }

    public Questoes save(Questoes questoes) {
        return questoesRepository.save(questoes);
    }

    public void deleteById(Long id) {
        questoesRepository.deleteById(id);
    }

    public List<Questoes> findQuestoesByPalestra(Long idPalestra) {
       return questoesRepository.findByIdPalestra(idPalestra);
}
}
