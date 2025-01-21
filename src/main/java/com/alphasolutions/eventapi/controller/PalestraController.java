package com.alphasolutions.eventapi.controller;

import org.springframework.web.bind.annotation.RestController;

import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.repository.PalestraRepository;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
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



}
