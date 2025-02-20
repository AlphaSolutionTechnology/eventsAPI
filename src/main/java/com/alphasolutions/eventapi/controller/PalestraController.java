package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.exception.*;
import com.alphasolutions.eventapi.model.Palestra;
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
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/palestra")
public class PalestraController {

    private final AuthService authService;
    private final PalestraRepository palestraRepository;
    private final PalestraService palestraService;
    private final UserService userService;

    public PalestraController(
            PalestraRepository palestraRepository,
            JwtUtil jwtUtil,
            PalestraService palestraService,
            UserRepository userRepository,
            AuthService authService, UserService userService) {
        this.palestraRepository = palestraRepository;
        this.palestraService = palestraService;
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping("/verificarPalestra/{uniqueCode}")
    public ResponseEntity<?> verificarPalestra(
            @PathVariable String uniqueCode,
            @CookieValue(value = "eventToken") String eventToken) {

        try {
            authService.authenticate(eventToken);
            User user = userService.getUserByToken(eventToken);
            Palestra palestra = palestraService.findPalestra(uniqueCode.trim());

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
            List<Palestra> palestras = palestraRepository.findAll();
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
            Palestra novaPalestra = palestraService.criarPalestra(palestra);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaPalestra);
        } catch (InvalidRoleException | InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Algo deu errado..");
        }

    }

    @DeleteMapping("/excluir")
    public ResponseEntity<?> excluirPalestras(@CookieValue("eventToken") String eventToken,@RequestBody PalestraIdsDTO dto) {
        System.out.println(dto.getId());
        Long id = Long.parseLong(dto.getId());
        System.out.println(id);
        try {
            authService.authenticateAdmin(eventToken);
            palestraService.deletePalestra(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (PalestraNotFoundException palestraNotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(palestraNotFoundException.getMessage());
        } catch (InvalidRoleException | InvalidTokenException invalidRoleException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso Negado.");
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
            palestraService.inscreverUsuarioNaPalestra(palestra.getId(),user);
            return ResponseEntity.ok(Map.of("message", "sucesso","idPalestra", palestra.getId()));
        } catch (InvalidTokenException invalidTokenException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(invalidTokenException.getMessage());
        } catch (PalestraNotFoundException|UserNotFoundException notFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Algo deu errado..");
        }
    }

    @PostMapping("/inscrever/{uniqueCode}")
    public ResponseEntity<?> inscreverUsuarioNaPalestra(
            @PathVariable String uniqueCode,
            @CookieValue(value = "eventToken") String eventToken) {

        try {
            authService.authenticate(eventToken);
            User user = userService.getUserByToken(eventToken);
            Palestra palestra = palestraService.findPalestra(uniqueCode.trim());
            if(palestraService.isUsuarioInscritoNaPalestra(palestra, user)){
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            palestraService.inscreverUsuarioNaPalestra(palestra.getId(), user);
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


}
