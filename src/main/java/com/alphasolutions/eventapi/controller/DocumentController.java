package com.alphasolutions.eventapi.controller;
import org.apache.tika.Tika;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/document")
public class DocumentController {

    private final Tika tika = new Tika();

    @PostMapping("/extract")
    public ResponseEntity<String> extractText(@RequestParam("file") MultipartFile file) {
        try {
            String text = tika.parseToString(file.getInputStream());
            return ResponseEntity.ok(text);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro ao extrair texto.");
        }
    }
}
