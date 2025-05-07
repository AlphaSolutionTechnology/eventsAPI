package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.exception.InvalidRoleException;
import com.alphasolutions.eventapi.exception.InvalidTokenException;
import com.alphasolutions.eventapi.exception.PalestraNotFoundException;
import com.alphasolutions.eventapi.exception.UserAlreadyExistsException;
import com.alphasolutions.eventapi.model.entity.Palestra;
import com.alphasolutions.eventapi.model.entity.Questoes;
import com.alphasolutions.eventapi.model.dto.QuestoesDTO;
import com.alphasolutions.eventapi.model.dto.ResultDTO;
import com.alphasolutions.eventapi.model.entity.Results;
import com.alphasolutions.eventapi.model.entity.User;
import com.alphasolutions.eventapi.model.dto.QuestoesPublicDTO;
import com.alphasolutions.eventapi.service.AuthService;
import com.alphasolutions.eventapi.service.PalestraService;
import com.alphasolutions.eventapi.service.QuestoesService;
import com.alphasolutions.eventapi.service.ResultService;
import com.alphasolutions.eventapi.service.UserService;
import com.alphasolutions.eventapi.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(QuestoesController.class);

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

    @PostMapping("/startquiz/{idPalestra}")
    public ResponseEntity<?> startQuiz(
            @CookieValue(value = "eventToken") String eventToken,
            @PathVariable Long idPalestra
    ) {
        try {
            logger.info("Iniciando quiz para idPalestra: {}", idPalestra);
            authService.authenticate(eventToken);
            String userId = jwtUtil.extractClaim(eventToken).get("id").toString();
            Results result = resultService.iniciarQuiz(userId, idPalestra);
            logger.info("Quiz iniciado com sucesso para userId: {}, idPalestra: {}", userId, idPalestra);
            return ResponseEntity.ok(result);
        } catch (InvalidTokenException e) {
            logger.error("Token inválido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (PalestraNotFoundException e) {
            logger.error("Palestra não encontrada: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro ao iniciar quiz: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro interno ao iniciar quiz"));
        }
    }

    @PostMapping("/finishquiz/{idPalestra}")
    public ResponseEntity<?> finishQuiz(
            @CookieValue(value = "eventToken") String eventToken,
            @RequestBody ResultDTO resultDTO,
            @PathVariable Long idPalestra
    ) {
        try {
            logger.info("Finalizando quiz para idPalestra: {}", idPalestra);
            authService.authenticate(eventToken);
            String userId = jwtUtil.extractClaim(eventToken).get("id").toString();
            Results updatedResult = resultService.finalizarQuiz(userId, idPalestra, resultDTO);
            logger.info("Quiz finalizado com sucesso para userId: {}, idPalestra: {}", userId, idPalestra);
            return ResponseEntity.ok(updatedResult);
        } catch (InvalidTokenException e) {
            logger.error("Token inválido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            logger.error("Erro ao finalizar quiz: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro interno ao finalizar quiz: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro interno ao finalizar quiz"));
        }
    }

    @GetMapping("/{idPalestra}")
    public ResponseEntity<?> getQuestoesByPalestra(
            @CookieValue("eventToken") String eventToken,
            @PathVariable Long idPalestra
    ) {
        try {
            logger.info("Buscando questões para idPalestra: {}", idPalestra);
            authService.authenticate(eventToken);
            List<Questoes> questoes = questoesService.findQuestoesByPalestra(idPalestra);
            List<QuestoesPublicDTO> dtos = questoes.stream()
                    .map(q -> new QuestoesPublicDTO(q.getId(), q.getEnunciado(), q.getChoices(), q.getIdPalestra()))
                    .collect(Collectors.toList());
            logger.info("Questões encontradas: {}", dtos.size());
            return ResponseEntity.ok(dtos);
        } catch (InvalidTokenException e) {
            logger.error("Token inválido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ArrayList<>());
        } catch (Exception e) {
            logger.error("Erro ao buscar questões: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ArrayList<>());
        }
    }

    @PostMapping("/createquestion")
    public ResponseEntity<?> createQuestao(
            @RequestBody Questoes questoes,
            @CookieValue(value = "eventToken") String eventToken
    ) {
        try {
            logger.info("Criando nova questão");
            if (questoes.getEnunciado() == null || questoes.getChoices() == null ||
                    questoes.getCorrectAnswer() == null || questoes.getEnunciado().isEmpty() ||
                    questoes.getChoices().isEmpty() || questoes.getCorrectAnswer().isEmpty()) {
                logger.warn("Dados da questão inválidos");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Dados da questão inválidos"));
            }
            authService.authenticateAdmin(eventToken);
            Questoes savedQuestao = questoesService.save(questoes);
            logger.info("Questão criada com sucesso: id: {}", savedQuestao.getId());
            return ResponseEntity.ok(savedQuestao);
        } catch (InvalidTokenException | InvalidRoleException e) {
            logger.error("Erro de autenticação: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro ao criar questão: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro interno ao criar questão"));
        }
    }

    @DeleteMapping("/delete/{idQuestao}")
    public ResponseEntity<?> deleteQuestao(
            @CookieValue(value = "eventToken") String token,
            @PathVariable Long idQuestao
    ) {
        try {
            logger.info("Deletando questão: idQuestao: {}", idQuestao);
            authService.authenticateAdmin(token);
            questoesService.deleteById(idQuestao);
            logger.info("Questão deletada com sucesso");
            return ResponseEntity.ok().build();
        } catch (InvalidTokenException | InvalidRoleException e) {
            logger.error("Erro de autenticação: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (NoSuchObjectException e) {
            logger.error("Questão não encontrada: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro ao deletar questão: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro interno ao deletar questão"));
        }
    }

    @PutMapping(value = "/updatequestion", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateQuestao(
            @CookieValue("eventToken") String eventToken,
            @RequestBody QuestoesDTO questoes
    ) {
        try {
            logger.info("Atualizando questão: id: {}", questoes.getIdUser());
            authService.authenticate(eventToken);
            questoesService.updateQuestoes(questoes);
            logger.info("Questão atualizada com sucesso");
            return ResponseEntity.ok().build();
        } catch (InvalidTokenException e) {
            logger.error("Token inválido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (NoSuchObjectException e) {
            logger.error("Questão não encontrada: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro ao atualizar questão: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro interno ao atualizar questão"));
        }
    }

    @GetMapping("/verificarStatus/{idPalestra}")
    public ResponseEntity<?> verificarStatusQuizz(
            @CookieValue("eventToken") String eventToken,
            @PathVariable Long idPalestra
    ) {
        try {
            logger.info("Verificando status do quiz para idPalestra: {}", idPalestra);
            authService.authenticate(eventToken);
            User user = userService.getUserByToken(eventToken);
            Palestra palestra = palestraService.findPalestraById(idPalestra);
            Optional<Results> result = resultService.findResultByUserAndPalestra(user, palestra);
            if (result.isEmpty()) {
                logger.warn("Nenhum resultado encontrado para userId: {}, idPalestra: {}", user.getIdUser(), idPalestra);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Nenhum quiz encontrado para esta palestra"));
            }
            boolean isPending = result.get().getEndTime() == null;
            logger.info("Status do quiz: {}", isPending ? "Pendente" : "Concluído");
            return ResponseEntity.ok(Map.of("quizzStatus", isPending ? "Pendente" : "Concluído"));
        } catch (InvalidTokenException e) {
            logger.error("Token inválido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro ao verificar status do quiz: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro interno ao verificar status"));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateAnswer(
            @CookieValue("eventToken") String eventToken,
            @RequestBody Map<String, Object> payload
    ) {
        try {
            logger.info("Validando resposta para questionId: {}", payload.get("questionId"));
            authService.authenticate(eventToken);
            Long questionId = Long.valueOf(payload.get("questionId").toString());
            String selectedAnswer = payload.get("selectedAnswer").toString();
            Questoes questao = questoesService.findById(questionId);
            boolean isCorrect = questao.getCorrectAnswer().equals(selectedAnswer);
            logger.info("Resposta válida: {}", isCorrect);
            return ResponseEntity.ok(Map.of("isCorrect", isCorrect));
        } catch (InvalidTokenException e) {
            logger.error("Token inválido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            logger.error("Questão não encontrada: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Questão não encontrada"));
        } catch (Exception e) {
            logger.error("Erro ao validar resposta: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro interno ao validar resposta"));
        }
    }

    @GetMapping("/duration")
    public ResponseEntity<?> getQuizDuration(
            @CookieValue("eventToken") String eventToken
    ) {
        try {
            logger.info("Buscando duração do quiz");
            authService.authenticate(eventToken);
            int duration = 300; // 5 minutos, ajustar se necessário
            logger.info("Duração retornada: {} segundos", duration);
            return ResponseEntity.ok(Map.of("duration", duration));
        } catch (InvalidTokenException e) {
            logger.error("Token inválido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro ao buscar duração: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro interno ao buscar duração"));
        }
    }
}