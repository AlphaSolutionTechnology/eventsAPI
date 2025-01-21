package com.alphasolutions.eventapi.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.DeleteExchange;

import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.model.PalestraIdsDTO;
import com.alphasolutions.eventapi.repository.PalestraRepository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;




@RestController
@RequestMapping("/api/palestra")
public class PalestraController {
    
private PalestraRepository palestraRepository;

public PalestraController(PalestraRepository palestraRepository){
    this.palestraRepository = palestraRepository;
}

@GetMapping("/lista")
public List<Palestra> PalestraList(){
    return palestraRepository.findAll();
}


@PostMapping("/criar")
public ResponseEntity<?> createPalestra(@RequestBody Palestra palestra) {

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

    if (ids.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Nenhuma palestra selecionada para exclusão.");
    }

    List<Long> idsNaoEncontrados = new ArrayList<>();
    for (Long id : ids) {
        if (palestraRepository.existsById(id)) {
            palestraRepository.deleteById(id);
        } else {
            idsNaoEncontrados.add(id);
        }
    }

    if (!idsNaoEncontrados.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("As palestras com os seguintes IDs não foram encontradas: " + idsNaoEncontrados);
    }

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); 
}




}


