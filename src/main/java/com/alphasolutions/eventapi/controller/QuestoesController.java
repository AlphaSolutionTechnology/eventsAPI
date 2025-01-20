package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.model.Questoes;
import com.alphasolutions.eventapi.service.QuestoesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questoes")
public class QuestoesController {

    @Autowired
    private QuestoesService questoesService;

    @GetMapping
    public List<Questoes> getAllQuestoes() {
        return questoesService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Questoes> getQuestaoById(@PathVariable Long id) {
        return questoesService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Questoes createQuestao(@RequestBody Questoes questoes) {
        return questoesService.save(questoes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Questoes> updateQuestao(@PathVariable Long id, @RequestBody Questoes updatedQuestoes) {
        return questoesService.findById(id)
                .map(existingQuestao -> {
                    updatedQuestoes.setId(existingQuestao.getId());
                    Questoes savedQuestao = questoesService.save(updatedQuestoes);
                    return ResponseEntity.ok(savedQuestao);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestao(@PathVariable Long id) {
        if (questoesService.findById(id).isPresent()) {
            questoesService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
