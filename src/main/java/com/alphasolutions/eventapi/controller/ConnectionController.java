package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.exception.InvalidTokenException;
import com.alphasolutions.eventapi.model.AlphaConnectionRequest;
import com.alphasolutions.eventapi.model.Conexao;
import com.alphasolutions.eventapi.model.ConexaoDTO;
import com.alphasolutions.eventapi.service.AuthService;
import com.alphasolutions.eventapi.utils.JwtUtil;
import com.alphasolutions.eventapi.websocket.service.ConnectionService;
import org.springframework.http.HttpStatus;
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
    SimpMessagingTemplate messagingTemplate;
    public ConnectionController(JwtUtil jwtUtil, ConnectionService connectionService, SimpMessagingTemplate messagingTemplate, AuthService authService) {
        this.connectionService = connectionService;
        this.messagingTemplate = messagingTemplate;
        this.authService = authService;
    }

    @GetMapping("/retrieveconnectionrequest")
    public ResponseEntity<?> retrieveConnectionRequest(@CookieValue(value = "eventToken") String token) {
        try {
            authService.auhenticate(token);
            List<Conexao> conexao = connectionService.getConexoes(token);
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


    @PatchMapping(value = "/answerconnectionrequest")
    public ResponseEntity<?> answerConnectionRequest(@CookieValue(value = "eventToken") String token, @RequestBody AlphaConnectionRequest connectionRequest) {
        try {
            authService.auhenticate(token);
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