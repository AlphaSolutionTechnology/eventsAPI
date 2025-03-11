package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.exception.InvalidRoleException;
import com.alphasolutions.eventapi.exception.InvalidTokenException;
import com.alphasolutions.eventapi.exception.PalestraNotFoundException;
import com.alphasolutions.eventapi.exception.UserAlreadyExistsException;
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
import java.util.*;
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

    @PostMapping("/startquiz/{idPalestra}")
    public ResponseEntity<?> startQuiz(@CookieValue(value = "eventToken") String eventToken,
                                       @PathVariable Long idPalestra) {
        try {
            authService.authenticate(eventToken);
            String userId = jwtUtil.extractClaim(eventToken).get("id").toString();
            Results result = resultService.iniciarQuiz(userId, idPalestra);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (InvalidTokenException invalidTokenException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: " + invalidTokenException.getMessage());
        } catch (PalestraNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        } catch (UserAlreadyExistsException e){
            return  ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/finishquiz/{idPalestra}")
    public ResponseEntity<?> finishQuiz(@CookieValue(value = "eventToken") String eventToken,
                                        @RequestBody ResultDTO result,
                                        @PathVariable Long idPalestra) {
        try {
            authService.authenticate(eventToken);
            String userId = jwtUtil.extractClaim(eventToken).get("id").toString();
            Results updatedResult = resultService.finalizarQuiz(userId, idPalestra, result);
            return ResponseEntity.status(HttpStatus.OK).body(updatedResult);
        } catch (InvalidTokenException invalidTokenException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: " + invalidTokenException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{idPalestra}")
    public ResponseEntity<?> getQuestoesByPalestra(@CookieValue("eventToken") String eventToken, @PathVariable Long idPalestra) {
        try {
            authService.authenticate(eventToken);
            List<Questoes> questoes = questoesService.findQuestoesByPalestra(idPalestra);
            List<QuestoesPublicDTO> dtos = questoes.stream()
                    .map(q -> new QuestoesPublicDTO(q.getId(), q.getEnunciado(), q.getChoices(), q.getIdPalestra()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ArrayList<>());
        }
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
            if(!result.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + palestra);
            }
            boolean isPending = result.isEmpty() || result.get().getEndTime() == null;
            return ResponseEntity.ok(Map.of("quizzStatus", isPending ? "Pendente" : "Concluído"));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateAnswer(@CookieValue("eventToken") String eventToken, @RequestBody Map<String, Object> payload) {
        try {
            authService.authenticate(eventToken);
            Long questionId = Long.valueOf(payload.get("questionId").toString());
            String selectedAnswer = payload.get("selectedAnswer").toString();
            Questoes questao = questoesService.findById(questionId);
            boolean isCorrect = questao.getCorrectAnswer().equals(selectedAnswer);
            return ResponseEntity.ok(Map.of("isCorrect", isCorrect));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: " + e.getMessage());
        } catch (NoSuchElementException noSuchElementException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + noSuchElementException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao validar resposta: " + e.getMessage());
        }
    }

    @GetMapping("/duration")
    public ResponseEntity<Map<String, Integer>> getQuizDuration(@CookieValue("eventToken") String eventToken) {
        try {
            authService.authenticate(eventToken);
            // Exemplo: retorna 300 segundos (5 minutos) como duração fixa
            int duration = 300; // Pode ser ajustado para ser dinâmico (ex.: baseado no número de questões)
            return ResponseEntity.ok(Map.of("duration", duration));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", -1));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", -1));
        }
    }
}