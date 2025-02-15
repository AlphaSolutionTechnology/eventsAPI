package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.model.Questoes;
import com.alphasolutions.eventapi.model.QuestoesDTO;
import com.alphasolutions.eventapi.model.ResultDTO;
import com.alphasolutions.eventapi.service.QuestoesService;

import com.alphasolutions.eventapi.service.ResultService;
import com.alphasolutions.eventapi.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api/questoes")
public class QuestoesController {

    private final QuestoesService questoesService;
    private final JwtUtil jwtUtil;
    private final ResultService resultService;

    public QuestoesController(QuestoesService questoesService, JwtUtil jwtUtil, ResultService resultService) {
        this.questoesService = questoesService;
        this.jwtUtil = jwtUtil;
        this.resultService = resultService;
    }

    @PostMapping("/registerresult")
    public ResponseEntity<?> registerResult(@CookieValue(value = "eventToken") String eventToken, @RequestBody ResultDTO result) {
        Map<String, Object> verifiedToken = jwtUtil.extractClaim(eventToken);
        if(verifiedToken.get("error") != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: " + verifiedToken.get("error"));
        }
        if (resultService.saveResult(result,verifiedToken.get("id").toString())) {
            return ResponseEntity.status(HttpStatus.OK).body("Salvo com sucesso!");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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

   @GetMapping("/{idPalestra}")
   public ResponseEntity<List<Questoes>> getQuestoesByPalestra(@PathVariable Long idPalestra) {
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
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
