package com.alphasolutions.eventapi.websocket.controller;

import com.alphasolutions.eventapi.websocket.service.NotificationService;
import com.alphasolutions.eventapi.websocket.utils.NotificationMessage;
import com.alphasolutions.eventapi.websocket.utils.NotificationRequest;
import com.alphasolutions.eventapi.websocket.utils.NotificationResponseMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketController {
    NotificationService notificationService;
    SimpMessagingTemplate messagingTemplate;
    public WebSocketController(NotificationService notificationService, SimpMessagingTemplate messagingTemplate) {
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
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
        String[] senderName = principal.getName().split(" ");
        NotificationRequest request = new NotificationRequest(senderName[0] + " Quer se conectar a você!");
        messagingTemplate.convertAndSendToUser(principal.getName(),"/queue/notification" ,response);
        System.out.println(response.getMessage());
        if(response.getMessage().equals("Sucesso!")){
            messagingTemplate.convertAndSendToUser(message.getTo(),"/queue/notification" ,request);
        }
    }
}
