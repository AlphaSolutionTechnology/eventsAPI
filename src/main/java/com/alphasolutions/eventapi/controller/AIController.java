package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.service.AIRequestService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.*;

@RestController
@RequestMapping("/api/AI")
public class AIController {

    AIRequestService aiRequestService;

    public AIController(AIRequestService aiRequestService) {
        this.aiRequestService = aiRequestService;
    }

    @PostMapping(value = "/requestquestion", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> chat(@RequestBody Map<String, Object> request) {
        try {
            List<Map<String,Object>> response = aiRequestService.sendRequestToGemini(request);
            return ResponseEntity.ok().body(response);
        }catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

