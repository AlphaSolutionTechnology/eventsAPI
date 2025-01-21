package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.service.ConnectionServiceImpl;

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
        Long idSolicitante = (Long)connectionRequest.get("idSolicitante");
        Long idSolicitado = (Long) connectionRequest.get("idSolicitado");

        if(connectionServiceImpl.isConnected(idSolicitante, idSolicitado)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Os usuários já estão conectados!"));
        }
        try {
            connectionServiceImpl.connect(idSolicitante, idSolicitado);

            return ResponseEntity.ok().build();
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }

    }
}
