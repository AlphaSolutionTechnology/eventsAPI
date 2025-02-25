// File: QuestoesController.java
package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.exception.InvalidRoleException;
import com.alphasolutions.eventapi.exception.InvalidTokenException;
import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.model.Questoes;
import com.alphasolutions.eventapi.model.QuestoesDTO;
import com.alphasolutions.eventapi.model.ResultDTO;
import com.alphasolutions.eventapi.model.Results;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.model.QuestoesPublicDTO;
import com.alphasolutions.eventapi.service.AuthService;
import com.alphasolutions.eventapi.service.PalestraService;
import com.alphasolutions.eventapi.service.QuestoesService;
import com.alphasolutions.eventapi.service.ResultService;
import com.alphasolutions.eventapi.service.UserService;
import com.alphasolutions.eventapi.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.rmi.NoSuchObjectException;
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
    private final AuthService authService;
    private final UserService userService;
    private final PalestraService palestraService;

    public QuestoesController(QuestoesService questoesService, JwtUtil jwtUtil, ResultService resultService, AuthService authService, UserService userService, PalestraService palestraService) {
        this.questoesService = questoesService;
        this.jwtUtil = jwtUtil;
        this.resultService = resultService;
        this.authService = authService;
        this.userService = userService;
        this.palestraService = palestraService;
    }

    @PostMapping("/registerresult/{idPalestra}")
    public ResponseEntity<?> registerResult(@CookieValue(value = "eventToken") String eventToken,
                                            @RequestBody ResultDTO result,
                                            @PathVariable Long idPalestra) {
        try {
            authService.authenticate(eventToken);
            resultService.saveResult(result, jwtUtil.extractClaim(eventToken).get("id").toString(), idPalestra);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (InvalidTokenException invalidTokenException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: " + invalidTokenException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // Endpoint usado somente por administradores – cuidado com o vazamento de dados
    @GetMapping("/retrieveallquestions")
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

    // Endpoint para obtenção das questões sem o campo "correctAnswer"
    @GetMapping("/{idPalestra}")
    public ResponseEntity<List<QuestoesPublicDTO>> getQuestoesByPalestra(@PathVariable Long idPalestra) {
        List<Questoes> questoes = questoesService.findQuestoesByPalestra(idPalestra);
        List<QuestoesPublicDTO> dtos = questoes.stream()
                .map(q -> new QuestoesPublicDTO(q.getId(), q.getEnunciado(), q.getChoices(), q.getIdPalestra()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/createquestion")
    public ResponseEntity<Questoes> createQuestao(@RequestBody Questoes questoes,
                                                  @CookieValue(value = "eventToken") String eventToken) {
        try {
            if (questoes.getEnunciado() == null || questoes.getChoices() == null ||
                questoes.getCorrectAnswer() == null || questoes.getEnunciado().isEmpty() ||
                questoes.getChoices().isEmpty() || questoes.getCorrectAnswer().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(questoes);
            }
            authService.authenticateAdmin(eventToken);
            return ResponseEntity.ok(questoesService.save(questoes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/delete/{idQuestao}")
    public ResponseEntity<String> deleteQuestao(@CookieValue(value = "eventToken") String token,
                                                @PathVariable Long idQuestao) {
        try {
            authService.authenticateAdmin(token);
            questoesService.deleteById(idQuestao);
            return ResponseEntity.ok().build();
        } catch (InvalidTokenException | InvalidRoleException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: " + e.getMessage());
        } catch (NoSuchObjectException noSuchObjectException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + noSuchObjectException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @PutMapping(value = "/updatequestion", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateQuestao(@CookieValue("eventToken") String eventToken,
                                                @RequestBody QuestoesDTO questoes) {
        try {
            authService.authenticate(eventToken);
            questoesService.updateQuestoes(questoes);
            return ResponseEntity.ok().build();
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: " + e.getMessage());
        } catch (NoSuchObjectException noSuchObjectException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + noSuchObjectException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/verificarStatus/{idPalestra}")
    public ResponseEntity<?> verificarStatusQuizz(@CookieValue("eventToken") String eventToken,
                                                  @PathVariable Long idPalestra) {
        try {
            authService.authenticate(eventToken);
            User user = userService.getUserByToken(eventToken);
            Palestra palestra = palestraService.findPalestraById(idPalestra);
            Optional<Results> result = resultService.findResultByUserAndPalestra(user, palestra);
            boolean notDone = result.isEmpty();
            return ResponseEntity.ok(Map.of("quizzStatus", notDone ? "Pendente" : "Concluído"));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Novo endpoint para validação da resposta, garantindo que a comparação seja feita no back-end
    @PostMapping("/validate")
    public ResponseEntity<?> validateAnswer(@RequestBody Map<String, Object> payload) {
        try {
            Long questionId = Long.valueOf(payload.get("questionId").toString());
            String selectedAnswer = payload.get("selectedAnswer").toString();

            Questoes questao = questoesService.findById(questionId);
            if (questao == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Questão não encontrada");
            }

            boolean isCorrect = questao.getCorrectAnswer().equals(selectedAnswer);
            return ResponseEntity.ok(Map.of("isCorrect", isCorrect));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao validar resposta: " + e.getMessage());
        }
    }
}
