package com.alphasolutions.eventapi.websocket.controller;

import com.alphasolutions.eventapi.repository.UserRepository;
import com.alphasolutions.eventapi.websocket.service.NotificationService;
import com.alphasolutions.eventapi.websocket.notification.NotificationMessage;
import com.alphasolutions.eventapi.websocket.notification.NotificationResponseMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.Map;

@Controller
public class WebSocketController {
    private final UserRepository userRepository;
    NotificationService notificationService;
    SimpMessagingTemplate messagingTemplate;
    public WebSocketController(NotificationService notificationService, SimpMessagingTemplate messagingTemplate, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
    }

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public String message(String message) {
        System.out.println("received message: " + message);
        return "system"+ message;
    }

    @MessageMapping("/sendrequest")
    public void notification(@Payload NotificationMessage message, Principal principal) {
        NotificationResponseMessage response = notificationService.askForConnection(principal.getName(),message.getTo());
        String[] senderName = {""};
        if(!response.getMessage().contains("encontrado")){
            senderName = userRepository.findByUniqueCode(message.getTo()).getNome().split(" ");
        }
        response.setTo(principal.getName());
        messagingTemplate.convertAndSendToUser(principal.getName(),"/queue/notification" ,response);
        System.out.println(response.getMessage());
        if(response.getMessage().equals("Sucesso!")){
            messagingTemplate.convertAndSendToUser(message.getTo(),"/queue/notification" , Map.of("to",message.getTo(), "from", principal.getName(),"message","quer se conectar a você","name",(senderName[0] + " "+ senderName[1])));
        }
    }
}
