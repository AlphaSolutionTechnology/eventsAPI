package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.model.Questoes;
import com.alphasolutions.eventapi.model.QuestoesDTO;
import com.alphasolutions.eventapi.service.QuestoesService;

import com.alphasolutions.eventapi.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/questoes")
public class QuestoesController {

    private final QuestoesService questoesService;
    private final JwtUtil jwtUtil;

    public QuestoesController(QuestoesService questoesService, JwtUtil jwtUtil) {
        this.questoesService = questoesService;
        this.jwtUtil = jwtUtil;
    }

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

   @GetMapping("/palestraQuizz")
   public ResponseEntity<List<Questoes>> getQuestoesByPalestra(@RequestParam Long idPalestra) {
    List<Questoes> questoes = questoesService.findQuestoesByPalestra(idPalestra); 
    return ResponseEntity.ok(questoes);
   }
   

    @PostMapping
    public ResponseEntity<Questoes> createQuestao(@RequestBody Questoes questoes) {
        return ResponseEntity.ok(questoesService.save(questoes));
    }

    @DeleteMapping("/delete/{idQuestao}")
    public ResponseEntity<String> deleteQuestao(@CookieValue(value = "eventToken") String token,@PathVariable Long idQuestao) {

        try{
            if(jwtUtil.extractClaim(token).get("error") != null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            questoesService.deleteById(idQuestao);
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping(value = "/update",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateQuestao(@RequestBody QuestoesDTO questoes) {
        try{
            questoesService.updateQuestoes(questoes);
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
