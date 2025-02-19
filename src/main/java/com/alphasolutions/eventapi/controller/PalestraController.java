package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.service.AuthService;
import com.alphasolutions.eventapi.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;


import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.model.PalestraIdsDTO;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.repository.PalestraRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
import com.alphasolutions.eventapi.service.PalestraService;
import com.alphasolutions.eventapi.service.RankingService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/palestra")
public class PalestraController {

    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final PalestraRepository palestraRepository;
    private final PalestraService palestraService;
    private final UserRepository userRepository;
    private final RankingService rankingService;

public PalestraController(PalestraRepository palestraRepository, JwtUtil jwtUtil, PalestraService palestraService, UserRepository userRepository, RankingService rankingService, AuthService authService){
    this.palestraRepository = palestraRepository;
    this.jwtUtil = jwtUtil;
    this.palestraService = palestraService;
    this.userRepository = userRepository;
    this.rankingService = rankingService;
    this.authService = authService;
}

@GetMapping("/lista")
public ResponseEntity<?> PalestraList(@CookieValue(value = "eventToken",required = true) String eventToken){
    try {
        authService.authenticateAdmin(eventToken);
        List<Palestra> palestras =  palestraRepository.findAll();
        return ResponseEntity.ok(palestras);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}

@GetMapping("/{uniqueCode}") //u
public ResponseEntity<?> validarPalestra(@PathVariable String uniqueCode, @CookieValue(value = "eventToken", required = true) String eventToken) {


    if (eventToken == null || eventToken.isEmpty()) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token inválido");
    }

    Map<String, Object> tokenVerified = jwtUtil.extractClaim(eventToken);

    if (tokenVerified.get("error") != null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro ao validar token.");
    }

    String userId = tokenVerified.get("id").toString();
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado.");
    }

    Optional<Palestra> palestra = palestraRepository.findByUniqueCode(uniqueCode.trim());
    if (palestra.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Palestra não encontrada.");
    }

    rankingService.inscreverUsuarioNoRanking(palestra.get(), user.get());

    Map<String, Object> response = new HashMap<>();
    response.put("message", "Palestra validada e usuário inscrito no ranking.");
    response.put("idPalestra", palestra.get().getId());  // Retorna o ID da palestra

    return ResponseEntity.ok(response);
}

@GetMapping("/verificarInscricao/{idPalestra}")
public ResponseEntity<?> verificarInscricao(@PathVariable Long idPalestra, @CookieValue(value = "eventToken", required = true) String eventToken) {

    if (eventToken == null || eventToken.isEmpty()) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token inválido.");
    }

    Map<String, Object> tokenVerified = jwtUtil.extractClaim(eventToken);
    if (tokenVerified.get("error") != null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro ao validar token.");
    }

    // Buscar o usuário no banco de dados com base no ID do token
    String userId = tokenVerified.get("id").toString();
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado.");
    }

    Optional<Palestra> palestra = palestraRepository.findById(idPalestra);
    if (palestra.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Palestra não encontrada.");
    }

    boolean isInscrito = rankingService.isUsuarioInscritoNoRanking(palestra.get(), user.get());
    
    if (isInscrito) {
        return ResponseEntity.ok(Map.of("inscrito", isInscrito));
    } else {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuário não está inscrito nesta palestra.");
    }
}

@PostMapping("/criar")
public ResponseEntity<?> createPalestra(@CookieValue(value = "eventToken",required = true) String eventToken ,@RequestBody Palestra palestra) {
    if(eventToken == null || eventToken.isEmpty()) {
        ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token invalido");
    }
    if(eventToken != null && !eventToken.isEmpty()) {
        Map<String, Object> tokenVerified = jwtUtil.extractClaim(eventToken);
        if(tokenVerified.get("error") != null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(tokenVerified.get("error").toString());
        }
        if(tokenVerified.get("role").equals("Participante")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não é palestrante");
        }
    }
    if (palestraRepository.existsByTema(palestra.getTema())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Uma palestra com este tema já existe.");
    }

    Palestra novaPalestra = palestraService.criarPalestra(palestra);

    return ResponseEntity.status(HttpStatus.CREATED).body(novaPalestra);
}


@DeleteMapping("/excluir")
public ResponseEntity<?> deletePalestras(@RequestBody PalestraIdsDTO dto) {
    List<Long> ids = dto.getIds();

    if (ids == null || ids.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Nenhuma palestra selecionada para exclusão.");
    }

    try {
        palestraRepository.deleteAllById(ids);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao excluir palestras: " + e.getMessage());
    }
}

@DeleteMapping("/desinscrever/{idPalestra}")
public ResponseEntity<?> desinscreverUsuarioDaPalestra(@PathVariable Long idPalestra, @CookieValue(value = "eventToken", required = true) String eventToken) {

    if (eventToken == null || eventToken.isEmpty()) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token inválido");
    }

   
    Map<String, Object> tokenVerified = jwtUtil.extractClaim(eventToken);
    if (tokenVerified.get("error") != null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro ao validar token.");
    }

   
    String userId = tokenVerified.get("id").toString();
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado.");
    }

    
    Optional<Palestra> palestra = palestraRepository.findById(idPalestra);
    if (palestra.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Palestra não encontrada.");
    }

    // Remover usuário do ranking da palestra
    rankingService.removerUsuarioDoRanking(palestra.get(), user.get());

    return ResponseEntity.ok("Usuário desinscrito da palestra e removido do ranking.");
}




}


