package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.Exception.AlreadyConnectedUsersException;
import com.alphasolutions.eventapi.Exception.UserNotFoundException;
import com.alphasolutions.eventapi.service.ConnectionServiceImpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/connection")
public class ConnectionController {

    private final ConnectionServiceImpl connectionServiceImpl;
    public ConnectionServiceImpl connectionService;


    public ConnectionController(ConnectionServiceImpl connectionService, ConnectionServiceImpl connectionServiceImpl) {
        this.connectionService = connectionService;
        this.connectionServiceImpl = connectionServiceImpl;
    }

    @PostMapping(value = "/sendconnection",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,String>> sendConnection(@RequestBody Map<Object,Object> connectionRequest) {
        String idSolicitante = connectionRequest.get("idSolicitante").toString();
        String idSolicitado = connectionRequest.get("idSolicitado").toString();
        if(idSolicitante.equals(idSolicitado)) {
            return ResponseEntity.badRequest().body(Map.of("message","Amor próprio é tudo mas não aqui meu nobre"));
        }

        try {
            connectionServiceImpl.connect(idSolicitante, idSolicitado);
            return ResponseEntity.ok().body(Map.of("message","Conectado com sucesso"));
        }catch (UserNotFoundException userNotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message",userNotFoundException.getMessage()));
        }catch (AlreadyConnectedUsersException alreadyConnectedUsersException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message",alreadyConnectedUsersException.getMessage()));
        }

    }
}
