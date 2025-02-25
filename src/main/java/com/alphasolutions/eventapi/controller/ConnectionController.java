package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.exception.*;
import com.alphasolutions.eventapi.model.*;
import com.alphasolutions.eventapi.service.AuthService;
import com.alphasolutions.eventapi.service.AuthorizationService;
import com.alphasolutions.eventapi.service.UserService;
import com.alphasolutions.eventapi.websocket.notification.NotificationResponseMessage;
import com.alphasolutions.eventapi.websocket.notification.Status;
import com.alphasolutions.eventapi.websocket.service.ConnectionService;
import org.apache.http.conn.ConnectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/connection")
public class ConnectionController {

    private final ConnectionService connectionService;
    private final AuthService authService;
    private final AuthorizationService authorizationService;
    private final UserService userService;
    SimpMessagingTemplate messagingTemplate;
    public ConnectionController(ConnectionService connectionService, SimpMessagingTemplate messagingTemplate, AuthService authService, AuthorizationService authorizationService, UserService userService) {
        this.connectionService = connectionService;
        this.messagingTemplate = messagingTemplate;
        this.authService = authService;
        this.authorizationService = authorizationService;
        this.userService = userService;
    }

    @GetMapping("/retrieveconnectionrequest")
    public ResponseEntity<?> retrieveConnectionRequest(@CookieValue(value = "eventToken") String token) {
        try {
            authService.authenticate(token);
            List<Conexao> conexao = connectionService.getPendingConnections(token);
            List<ConexaoDTO> connectionDto = new ArrayList<>(conexao.size());
            for(Conexao con : conexao) {
                connectionDto.add(new ConexaoDTO(con.getSolicitante().getUniqueCode(),con.getSolicitante().getNome()));
            }
            return ResponseEntity.ok().body(Map.of("server",connectionDto));
        }catch (InvalidTokenException invalidTokenException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/sendconnectionrequest", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendConnectionRequest(@CookieValue(value = "eventToken") String token, @RequestBody ConnectionRequestData requestData ){
        try {
            authService.authenticate(token);
            authorizationService.isUserSolicitant(token,requestData.getFrom());
            connectionService.connect(requestData.getFrom(), requestData.getTo(), Status.WAITING);
            User user = userService.getUserByToken(token);
            String[] senderName = user.getNome().split(" ");
            messagingTemplate.convertAndSendToUser(requestData.getTo(),"/queue/notification",Map.of("to", requestData.getTo(), "from", requestData.getFrom() ,"name",senderName[0] + (senderName.length >= 2 ? senderName[1]:""),"message",senderName[0]+ " "+ (senderName.length >= 2 ? senderName[1]:"") + " quer se conectar com você!"));
            return ResponseEntity.ok().body(Map.of("server","Enviada com Sucesso!"));
        } catch (AlreadyConnectedUsersException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("server", exception.getMessage()));
        } catch (WaitingForResponseException waitingForResponseException) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(Map.of("server",waitingForResponseException.getMessage()));
        }catch (UserNotMatchWithRequestException userNotMatchWithRequestException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("server",userNotMatchWithRequestException.getMessage()));
        }  catch (UserNotFoundException userNotFoundException){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("server",userNotFoundException.getMessage()));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("server",e.getMessage()));
        }catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("server",exception.getMessage()));
        }
    }


    @GetMapping("/getacceptedconnections")
    public ResponseEntity<?> getAcceptedConnections(@CookieValue(value = "eventToken") String token) {
        try {
            authService.authenticate(token);
            return  ResponseEntity.ok(connectionService.getAcceotedConnections(token));
        }catch (InvalidTokenException invalidTokenException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    @PatchMapping(value = "/answerconnectionrequest")
    public ResponseEntity<?> answerConnectionRequest(@CookieValue(value = "eventToken") String token, @RequestBody AlphaConnectionRequest connectionRequest) {
        try {
            authService.authenticate(token);
            connectionService.answerConnectionRequest(connectionRequest.getTo(),connectionRequest.getFrom(),connectionRequest.getStatus());
            return ResponseEntity.ok().build();
        }catch (InvalidTokenException invalidTokenException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (NullPointerException nullPointerException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}