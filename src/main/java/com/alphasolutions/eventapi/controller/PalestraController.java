package com.alphasolutions.eventapi.controller;

import org.springframework.web.bind.annotation.RestController;

import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.repository.PalestraRepository;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;




@RestController
@RequestMapping("/api/palestra")
public class PalestraController {
    
private PalestraRepository palestraRepository;


@GetMapping("/lista")
public List<Palestra> PalestraList(){
    return palestraRepository.findAll();
}


@PostMapping("/criar")
public Palestra createPalestra(@RequestBody Palestra palestra){
    return palestraRepository.save(palestra);
}



}
