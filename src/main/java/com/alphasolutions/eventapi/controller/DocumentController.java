package com.alphasolutions.eventapi.controller;

import org.apache.tika.Tika;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/document")
public class DocumentController {

    private final Tika tika = new Tika();

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; 

    @PostMapping("/extract")
    public ResponseEntity<String> extractText(@RequestParam("file") MultipartFile file) {
        try {

            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity
                        .status(HttpStatus.PAYLOAD_TOO_LARGE) 
                        .body("Arquivo muito grande! O limite permitido é 10MB.");
            }

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Arquivo vazio.");
            }

            
            String text = tika.parseToString(file.getInputStream());
            return ResponseEntity.ok(text);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro ao extrair texto.");
        }
    }
}
