package com.alphasolutions.eventapi.controller;
import com.alphasolutions.eventapi.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;
import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.model.PalestraIdsDTO;
import com.alphasolutions.eventapi.repository.PalestraRepository;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/palestra")
public class PalestraController {

    private final JwtUtil jwtUtil;
    private PalestraRepository palestraRepository;

public PalestraController(PalestraRepository palestraRepository, JwtUtil jwtUtil){
    this.palestraRepository = palestraRepository;
    this.jwtUtil = jwtUtil;
}

@GetMapping("/lista")
public List<Palestra> PalestraList(@CookieValue(value = "eventToken",required = true) String eventToken){
    if(eventToken == null || eventToken.isEmpty()) {
        ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token invalido");
    }
    if(eventToken != null && !eventToken.isEmpty()) {
        Map<String, Object> tokenVerified = jwtUtil.extractClaim(eventToken);
        if(tokenVerified.get("error") != null){
            return null;
        }
        if(tokenVerified.get("role").equals("Participante")) {
            return null;
        }
    }
    return palestraRepository.findAll();
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

    Palestra novaPalestra = palestraRepository.save(palestra);
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





}


