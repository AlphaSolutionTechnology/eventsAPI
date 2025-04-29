
package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.entity.Questoes;
import com.alphasolutions.eventapi.model.dto.QuestoesDTO;
import com.alphasolutions.eventapi.repository.QuestoesRepository;
import org.springframework.stereotype.Service;

import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class QuestoesService {

    private final QuestoesRepository questoesRepository;

    public QuestoesService(QuestoesRepository questoesRepository) {
        this.questoesRepository = questoesRepository;
    }

    public List<Questoes> findAll() {
        return questoesRepository.findAll();
    }

    public Questoes findById(Long id) {
        Questoes questoes =  questoesRepository.findById(id).orElse(null);

        if (questoes == null) {
            throw new NoSuchElementException("Não encontrado");
        }
        return questoes;
    }

    public Questoes save(Questoes questoes) {
        return questoesRepository.save(questoes);
    }

    public void deleteById(Long id) throws Exception {
        if (questoesRepository.existsById(id)) {
            questoesRepository.deleteById(id);
        } else {
            throw new NoSuchObjectException("Questions not found");
        }
    }

    public void updateQuestoes(QuestoesDTO questoesDTO) throws Exception {
        Questoes questao = questoesRepository.findById(questoesDTO.getIdQuestao()).orElse(null);
        if (questao == null) {
            throw new NoSuchObjectException("Questão não encontrada");
        }
        Questoes newQuestion = new Questoes(
                questoesDTO.getIdQuestao(),
                questoesDTO.getEnunciado(),
                questoesDTO.getChoices(),
                questoesDTO.getCorrectAnswer(),
                questoesDTO.getIdPalestra()
        );
        questoesRepository.save(newQuestion);
    }

    public List<Questoes> findQuestoesByPalestra(Long idPalestra) {
        return questoesRepository.findByIdPalestra(idPalestra);
    }
}
