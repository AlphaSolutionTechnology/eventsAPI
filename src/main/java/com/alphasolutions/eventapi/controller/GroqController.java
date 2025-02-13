package com.alphasolutions.eventapi.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.*;

@RestController
@RequestMapping("/api/chat")
public class GroqController {

    @Value("${groq.api.key}") // Pega a chave da API do application.properties
    private String groqApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping
public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> request) {
    System.out.println("API Key carregada: " + (groqApiKey != null && !groqApiKey.isEmpty() ? "OK" : "FALHA"));

    if (groqApiKey == null || groqApiKey.isEmpty()) {
        return ResponseEntity.status(500).body(Map.of("error", "API Key não configurada no servidor."));
    }

    String userMessage = request.get("message");

    Map<String, Object> payload = new HashMap<>();
    payload.put("model", "llama-3.3-70b-versatile");
    payload.put("messages", List.of(Map.of("role", "user", "content", userMessage)));

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + groqApiKey);
    headers.set("Content-Type", "application/json");

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

    try {
        ResponseEntity<Map> response = restTemplate.exchange(
            "https://api.groq.com/openai/v1/chat/completions",
            HttpMethod.POST,
            entity,
            Map.class
        );

        return ResponseEntity.ok(response.getBody());
    } catch (Exception e) {
        return ResponseEntity.status(500).body(Map.of("error", "Erro ao chamar API Groq: " + e.getMessage()));
    }
}
}
