package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.model.Questoes;
import com.alphasolutions.eventapi.service.QuestoesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/questoes")
public class QuestoesController {

    @Autowired
    private QuestoesService questoesService;

    @GetMapping
    public List<Map<String, Object>> getAllQuestoes() {
        return questoesService.findAll().stream()
                .map(questao -> Map.of(
                        "id", questao.getId(),
                        "question", questao.getEnunciado(),
                        "choices", questao.getChoices(),
                        "correctAnswer", questao.getCorrectAnswer()
                ))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<Questoes> createQuestao(@RequestBody Questoes questoes) {
        return ResponseEntity.ok(questoesService.save(questoes));
    }
}
