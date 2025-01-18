package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.configuration.ChatService;
import com.alphasolutions.eventapi.controller.ChatResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/google-gemini")
public class GeminiChatController {
    private final ChatService ChatService;

    public GeminiChatController(ChatService ChatService) {
        this.ChatService = ChatService;
    }

    @PostMapping(path = "/chat",consumes = "application/json")
    public  void chat(@RequestBody String message) {
        System.out.println(message);
    }
}
