package com.alphasolutions.eventapi.websocket.controller;

import com.alphasolutions.eventapi.repository.UserRepository;
import com.alphasolutions.eventapi.service.AuthService;
import com.alphasolutions.eventapi.websocket.service.NotificationService;
import com.alphasolutions.eventapi.websocket.notification.NotificationMessage;
import com.alphasolutions.eventapi.websocket.notification.NotificationResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;

import java.security.Principal;
import java.util.Map;

@Controller
public class connectionRequestWebSocketController {
    private final UserRepository userRepository;
    private final AuthService authService;
    NotificationService notificationService;
    SimpMessagingTemplate messagingTemplate;
    public connectionRequestWebSocketController(NotificationService notificationService, SimpMessagingTemplate messagingTemplate, UserRepository userRepository, AuthService authService) {
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @MessageMapping("/sendrequest")
    public void notification(@Payload NotificationMessage message, Principal principal, @CookieValue(value = "eventToken") String eventToken) {
        NotificationResponseMessage response = notificationService.askForConnection(principal.getName(),message.getTo());
        String[] senderName = {""};
        if(!response.getMessage().contains("encontrado")){
            senderName = userRepository.findByUniqueCode(message.getTo()).getNome().split(" ");
        }
        response.setTo(principal.getName());
        messagingTemplate.convertAndSendToUser(principal.getName(),"/queue/notification" ,response);
        if(response.getMessage().equals("Sucesso!")){
            messagingTemplate.convertAndSendToUser(message.getTo(),"/queue/notification" , Map.of("to",message.getTo(), "from", principal.getName(),"message","quer se conectar a você","name",(senderName[0] + " "+ senderName[1])));
        }
    }
}
