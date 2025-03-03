package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.exception.InvalidRoleException;
import com.alphasolutions.eventapi.exception.InvalidTokenException;
import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.model.Questoes;
import com.alphasolutions.eventapi.model.QuestoesDTO;
import com.alphasolutions.eventapi.model.QuestoesPublicDTO;
import com.alphasolutions.eventapi.model.Results;
import com.alphasolutions.eventapi.model.User;
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
import java.util.HashMap;
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

    public QuestoesController(
            QuestoesService questoesService,
            JwtUtil jwtUtil,
            ResultService resultService,
            AuthService authService,
            UserService userService,
            PalestraService palestraService
    ) {
        this.questoesService = questoesService;
        this.jwtUtil = jwtUtil;
        this.resultService = resultService;
        this.authService = authService;
        this.userService = userService;
        this.palestraService = palestraService;
    }

    @PostMapping("/validateAndRecord")
    public ResponseEntity<?> validateAndRecord(
            @CookieValue("eventToken") String eventToken,
            @RequestBody Map<String, Object> payload
    ) {
        try {
            authService.authenticate(eventToken);
            String userId = jwtUtil.extractClaim(eventToken).get("id").toString();
            Object answersObj = payload.get("answers");
            if (answersObj == null) {
                return ResponseEntity.badRequest().body("Faltando array 'answers'");
            }
            List<Map<String, Object>> answersList = (List<Map<String, Object>>) answersObj;
            for (Map<String, Object> singleAnswer : answersList) {
                if (!singleAnswer.containsKey("questionId")) {
                    return ResponseEntity.badRequest().body("Faltando campo 'questionId' em alguma resposta");
                }
                if (!singleAnswer.containsKey("selectedAnswer")) {
                    return ResponseEntity.badRequest().body("Faltando campo 'selectedAnswer' em alguma resposta");
                }
                Long questionId = Long.valueOf(singleAnswer.get("questionId").toString());
                String selectedAnswer = singleAnswer.get("selectedAnswer").toString();
                double timeSpent = 0.0;
                if (singleAnswer.containsKey("timeSpent")) {
                    timeSpent = Double.valueOf(singleAnswer.get("timeSpent").toString());
                }
                Questoes questao = questoesService.findById(questionId);
                if (questao == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Questão não encontrada: " + questionId);
                }
                boolean isCorrect = questao.getCorrectAnswer().equals(selectedAnswer);
                resultService.updateResult(userId, questao.getIdPalestra(), isCorrect, timeSpent);
            }
            boolean finalQuiz = false;
            if (payload.containsKey("final") && (boolean) payload.get("final")) {
                finalQuiz = true;
            }
            if (!finalQuiz) {
                return ResponseEntity.ok(Map.of("batchProcessed", true));
            }
            double totalTimeFromPayload = 0.0;
            if (payload.containsKey("totalTime")) {
                totalTimeFromPayload = Double.valueOf(payload.get("totalTime").toString());
            }
            Object palestraIdObj = payload.get("idPalestra");
            if (palestraIdObj == null) {
                return ResponseEntity.badRequest().body("Falta 'idPalestra' no payload");
            }
            Long idPalestra = Long.valueOf(palestraIdObj.toString());
            resultService.setFinalTotalTime(userId, idPalestra, totalTimeFromPayload);
            User user = userService.getUserByToken(eventToken);
            Palestra palestra = palestraService.findPalestraById(idPalestra);
            Optional<Results> optResults = resultService.findResultByUserAndPalestra(user, palestra);
            if (optResults.isEmpty()) {
                return ResponseEntity.ok(Map.of("batchProcessed", true));
            }
            Results finalResults = optResults.get();
            long totalSec = Math.round(finalResults.getTotalTime() != null ? finalResults.getTotalTime() : 0.0);
            long minutes = totalSec / 60;
            long seconds = totalSec % 60;
            String timeFormatted = minutes + " minuto(s) e " + seconds + " segundo(s)";
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("correctAnswers", finalResults.getCorrectAnswers());
            responseData.put("wrongAnswers", finalResults.getWrongAnswers());
            responseData.put("totalTime", timeFormatted);
            responseData.put("score", finalResults.getScore() != null ? finalResults.getScore() : 0);
            return ResponseEntity.ok(responseData);

        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Erro de token: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/{idPalestra}")
    public ResponseEntity<List<QuestoesPublicDTO>> getQuestoesByPalestra(@PathVariable Long idPalestra) {
        List<Questoes> questoes = questoesService.findQuestoesByPalestra(idPalestra);
        List<QuestoesPublicDTO> dtos = questoes.stream()
                .map(q -> new QuestoesPublicDTO(
                        q.getId(),
                        q.getEnunciado(),
                        q.getChoices(),
                        q.getIdPalestra()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/createquestion")
    public ResponseEntity<Questoes> createQuestao(
            @RequestBody Questoes questoes,
            @CookieValue(value = "eventToken") String eventToken
    ) {
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
    public ResponseEntity<String> deleteQuestao(
            @CookieValue(value = "eventToken") String token,
            @PathVariable Long idQuestao
    ) {
        try {
            authService.authenticateAdmin(token);
            questoesService.deleteById(idQuestao);
            return ResponseEntity.ok().build();
        } catch (InvalidTokenException | InvalidRoleException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: " + e.getMessage());
        } catch (NoSuchObjectException noSuchObjectException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + noSuchObjectException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PutMapping(value = "/updatequestion", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateQuestao(
            @CookieValue("eventToken") String eventToken,
            @RequestBody QuestoesDTO questoes
    ) {
        try {
            authService.authenticate(eventToken);
            questoesService.updateQuestoes(questoes);
            return ResponseEntity.ok().build();
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: " + e.getMessage());
        } catch (NoSuchObjectException noSuchObjectException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + noSuchObjectException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/verificarStatus/{idPalestra}")
    public ResponseEntity<?> verificarStatusQuizz(
            @CookieValue("eventToken") String eventToken,
            @PathVariable Long idPalestra
    ) {
        try {
            authService.authenticate(eventToken);
            User user = userService.getUserByToken(eventToken);
            Palestra palestra = palestraService.findPalestraById(idPalestra);
            Optional<Results> result = resultService.findResultByUserAndPalestra(user, palestra);
            boolean notDone = result.isEmpty();
            return ResponseEntity.ok(Map.of("quizzStatus", notDone ? "Pendente" : "Concluído"));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/duration")
    public ResponseEntity<Map<String, Object>> getQuizDuration(@CookieValue("eventToken") String eventToken) {
        try {
            authService.authenticate(eventToken);
            int durationInSeconds = 180;
            return ResponseEntity.ok(Map.of("duration", durationInSeconds));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
