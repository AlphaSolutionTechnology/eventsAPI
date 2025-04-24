package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.exception.*;
import com.alphasolutions.eventapi.model.entity.Palestra;
import com.alphasolutions.eventapi.model.dto.PalestraDTO;
import com.alphasolutions.eventapi.model.dto.QuizzStatusResponse;
import com.alphasolutions.eventapi.model.entity.User;
import com.alphasolutions.eventapi.repository.PalestraRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
import com.alphasolutions.eventapi.service.AuthService;
import com.alphasolutions.eventapi.service.PalestraService;
import com.alphasolutions.eventapi.service.QuizzSchedulerService;
import com.alphasolutions.eventapi.service.UserService;
import com.alphasolutions.eventapi.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;



@RestController
@RequestMapping("/api/palestra")
public class PalestraController {

    private final AuthService authService;
    private final PalestraRepository palestraRepository;
    private final PalestraService palestraService;
    private final UserService userService;
    private final QuizzSchedulerService quizzSchedulerService;
    private final SimpMessagingTemplate messagingTemplate;
  

    public PalestraController(
            PalestraRepository palestraRepository,
            JwtUtil jwtUtil,
            PalestraService palestraService,
            UserRepository userRepository,
            AuthService authService,
            UserService userService,
            QuizzSchedulerService quizzSchedulerService,
            SimpMessagingTemplate messagingTemplate) {    
        this.palestraRepository = palestraRepository;
        this.palestraService = palestraService;
        this.authService = authService;
        this.userService = userService;
        this.quizzSchedulerService = quizzSchedulerService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/verificarPalestra/{uniqueCode}")
    public ResponseEntity<?> verificarPalestra(
            @PathVariable Long uniqueCode,
            @CookieValue(value = "eventToken") String eventToken) {

        try {
            authService.authenticate(eventToken);
            User user = userService.getUserByToken(eventToken);
            Palestra palestra = palestraService.findPalestraById(uniqueCode);
            boolean isInscrito = palestraService.isUsuarioInscritoNaPalestra(palestra, user);

            return ResponseEntity.ok(Map.of(
                    "message", "Verificação concluída com sucesso.",
                    "idPalestra", palestra.getIdPalestra(),
                    "inscrito", isInscrito
            ));

        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (PalestraNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Algo deu errado...");
        }
    }


    @GetMapping("/lista")
    public ResponseEntity<?> listarPalestras(@CookieValue(value = "eventToken") String eventToken) {
        try {
            authService.authenticateAdmin(eventToken);
            List<PalestraDTO> palestras = palestraService.findAllUserPalestra(userService.getUserByToken(eventToken));
            return ResponseEntity.ok(palestras);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        }
    }

    @GetMapping("/lecturelist")
    public ResponseEntity<?> listarLectures(@CookieValue(value = "eventToken") String eventToken) {
        try {
            authService.authenticate(eventToken);
            List<PalestraDTO> palestras = palestraService.findAllPalestras();
            return ResponseEntity.ok(palestras);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        }
    }

    @PostMapping("/criar")
    public ResponseEntity<?> criarPalestra(
            @CookieValue(value = "eventToken") String eventToken,
            @RequestBody Palestra palestra) {

        try{
            authService.authenticateAdmin(eventToken);
            Palestra novaPalestra = palestraService.criarPalestra(palestra, eventToken);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaPalestra);
        } catch (InvalidRoleException | InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Algo deu errado..");
        }

    }

    @DeleteMapping("/excluir")
    public ResponseEntity<?> excluirPalestras(@CookieValue("eventToken") String eventToken) {

        try {
            authService.authenticateAdmin(eventToken);
            palestraService.unsubscribeAllUsersFromPalestra(1L);
            palestraService.deletePalestra(1L);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (PalestraNotFoundException palestraNotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(palestraNotFoundException.getMessage());
        } catch (InvalidRoleException | InvalidTokenException invalidRoleException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso Negado.");
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("NUmero inválido");
        }
    }


    @GetMapping("/{uniqueCode}")
    public ResponseEntity<?> validarPalestra(
            @PathVariable String uniqueCode,
            @CookieValue(value = "eventToken") String eventToken) {

        try{
            authService.authenticate(eventToken);
            User user = userService.getUserByToken(eventToken);
            Palestra palestra = palestraService.findPalestra(uniqueCode.trim());
            boolean isUserSubscribed = palestraService.isUsuarioInscritoNaPalestra(palestra,user);
            if (isUserSubscribed) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            palestraService.inscreverUsuarioNaPalestra(palestra,user);
            return ResponseEntity.ok(Map.of("message", "sucesso","idPalestra", palestra.getIdPalestra()));
        } catch (InvalidTokenException invalidTokenException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(invalidTokenException.getMessage());
        } catch (PalestraNotFoundException|UserNotFoundException notFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Algo deu errado..");
        }
    }

    @PatchMapping("/inscrever/{uniqueCode}")
    public ResponseEntity<?> inscreverUsuarioNaPalestra(
            @PathVariable String uniqueCode,
            @CookieValue(value = "eventToken") String eventToken) {
        try {
            authService.authenticate(eventToken);
            User user = userService.getUserByToken(eventToken);
            Palestra palestra = palestraService.findPalestra(uniqueCode.trim());
            if(palestraService.isUsuarioInscritoNaPalestra(palestra, user)){
                return ResponseEntity.ok(Map.of("message", "Usuário já inscrito", "idPalestra", palestra.getIdPalestra()));
            }
            palestraService.inscreverUsuarioNaPalestra(palestra, user);
            return ResponseEntity.ok(Map.of("message", "Usuário inscrito com sucesso!", "idPalestra", palestra.getIdPalestra()));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (PalestraNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Algo deu errado...");
        }
    }

    @DeleteMapping("/desinscrever/{idPalestra}")
    public ResponseEntity<?> desinscreverUsuarioDaPalestra(
            @PathVariable Long idPalestra,
            @CookieValue(value = "eventToken") String eventToken) {

        try {
            authService.authenticate(eventToken);
            User user = userService.getUserByToken(eventToken);
            palestraService.desinscreverUsuarioDaPalestra(user);
            return ResponseEntity.ok(Map.of("message", "Usuário desinscrito com sucesso!", "idPalestra", idPalestra));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (PalestraNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Algo deu errado...");
        }
    }


    @PostMapping("/liberar")
    public ResponseEntity<?> liberarQuizz(@CookieValue(value = "eventToken")String eventToken, @RequestBody Map<String, Object> requestBody) {
        try {
            authService.authenticate(eventToken);
            
            // Recuperando os parâmetros do corpo da requisição
            Long palestraId = Long.valueOf(requestBody.get("palestraId").toString());
            String horaProgramadaStr = (String) requestBody.get("horaProgramada");
            Timestamp horaLiberacao = horaProgramadaStr != null ? Timestamp.valueOf(horaProgramadaStr) : null;
            
            quizzSchedulerService.liberarOuAgendarQuizz(palestraId, horaLiberacao);
            
            LocalDateTime horaConvertida = horaLiberacao.toLocalDateTime();
            LocalDateTime agora = LocalDateTime.now();
    
            if (horaConvertida.equals(agora) || horaConvertida.isBefore(agora)) {
                messagingTemplate.convertAndSend("/topic/quizz-liberado", Map.of(
                "type", "quiz_liberado",
                "idPalestra", palestraId
            ));

                return ResponseEntity.ok("Quiz liberado com sucesso!");
            } else {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Quiz agendado para liberação.");
            }
        } catch (QuizException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao liberar o quiz: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro inesperado: " + e.getMessage());
        }
    
    }
    

    @GetMapping("/isReleased/{idPalestra}")
    public ResponseEntity<?> verificarQuizzLiberado(@CookieValue(value = "eventToken") String eventToken, @PathVariable Long idPalestra) {
        try{
            authService.authenticate(eventToken);
            
            Palestra palestra = palestraService.findPalestraById(idPalestra);
            OffsetDateTime horaLiberacao = palestra.getHoraLiberacao();
            boolean isQuizzReleased = palestra.getQuizzLiberado();
    
            if(isQuizzReleased){
                return ResponseEntity.ok(new QuizzStatusResponse("Quizz está liberado!"));
            } else if (!isQuizzReleased && horaLiberacao != null){
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(new QuizzStatusResponse("Quizz ainda não foi liberado."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new QuizzStatusResponse("isQuizzReleased é falso e horaLiberação null."));
            } 
        } catch (QuizException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new QuizzStatusResponse("Ocorreu um erro com o quizz: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new QuizzStatusResponse("Ocorreu um erro inesperado: " + e.getMessage()));
        }




    }

}

