package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.exception.*;
import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.model.PalestraDTO;
import com.alphasolutions.eventapi.model.PalestraIdsDTO;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.repository.PalestraRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
import com.alphasolutions.eventapi.service.AuthService;
import com.alphasolutions.eventapi.service.PalestraService;
import com.alphasolutions.eventapi.service.RankingService;
import com.alphasolutions.eventapi.service.UserService;
import com.alphasolutions.eventapi.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;


@RestController
@RequestMapping("/api/palestra")
public class PalestraController {

    private final AuthService authService;
    private final PalestraRepository palestraRepository;
    private final PalestraService palestraService;
    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public PalestraController(
            PalestraRepository palestraRepository,
            JwtUtil jwtUtil,
            PalestraService palestraService,
            UserRepository userRepository,
            AuthService authService, UserService userService,
            SimpMessagingTemplate simpMessagingTemplate) {
        this.palestraRepository = palestraRepository;
        this.palestraService = palestraService;
        this.authService = authService;
        this.userService = userService;
        this.simpMessagingTemplate =simpMessagingTemplate;
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
                    "idPalestra", palestra.getId(),
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
            List<Palestra> palestras = palestraService.findAllPalestras();
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
    public ResponseEntity<?> excluirPalestras(@CookieValue("eventToken") String eventToken,@RequestBody PalestraIdsDTO dto) {

        try {
            Long id = Long.parseLong(dto.getId());
            authService.authenticateAdmin(eventToken);
            palestraService.unsubscribeAllUsersFromPalestra(id);
            palestraService.deletePalestra(id);
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
            return ResponseEntity.ok(Map.of("message", "sucesso","idPalestra", palestra.getId()));
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
                return ResponseEntity.ok(Map.of("message", "Usuário já inscrito", "idPalestra", palestra.getId()));
            }
            palestraService.inscreverUsuarioNaPalestra(palestra, user);
            return ResponseEntity.ok(Map.of("message", "Usuário inscrito com sucesso!", "idPalestra", palestra.getId()));
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
            
            boolean liberado = palestraService.liberarQuiz(palestraId, horaLiberacao);
    
            if (liberado) {
                simpMessagingTemplate.convertAndSend("/topic/quizz-liberado", palestraId);
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
    

    

}
