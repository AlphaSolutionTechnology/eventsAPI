package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.model.AlphaConnectionRequest;
import com.alphasolutions.eventapi.model.Conexao;
import com.alphasolutions.eventapi.model.ConexaoDTO;
import com.alphasolutions.eventapi.utils.JwtUtil;
import com.alphasolutions.eventapi.websocket.service.ConnectionServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/connection")
public class ConnectionController {

    private final JwtUtil jwtUtil;
    private final ConnectionServiceImpl connectionServiceImpl;

    public ConnectionController(JwtUtil jwtUtil, ConnectionServiceImpl connectionServiceImpl) {
        this.jwtUtil = jwtUtil;
        this.connectionServiceImpl = connectionServiceImpl;
    }

    @GetMapping("/retrieveconnectionrequest")
    public ResponseEntity<Map<String, Object>> retrieveConnectionRequest(@CookieValue(value = "eventToken") String token) {
        Map<String,Object> validatedToken = jwtUtil.extractClaim(token);
        if (validatedToken.get("error") == null) {
            String userId = validatedToken.get("id").toString();
            List<Conexao> conexao = connectionServiceImpl.getThisUserConnectionRequest(userId);
            List<ConexaoDTO> connectionDto = new ArrayList<>(conexao.size());
            for (Conexao con : conexao) {
                System.out.println(con.toString());
                connectionDto.add(new ConexaoDTO(con.getSolicitante().getUniqueCode(),con.getSolicitante().getNome()));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("server",connectionDto));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("server","Invalid token"));
    }


    @PatchMapping(value = "/answerconnectionrequest")
    public ResponseEntity<String> answerConnectionRequest(@CookieValue(value = "eventToken") String token, @RequestBody AlphaConnectionRequest connectionRequest) {
        System.out.println(connectionRequest.toString());
        Map<String,Object> validatedToken = jwtUtil.extractClaim(token);
        if (validatedToken.get("error") == null) {
            if(validatedToken.get("unique_code").equals(connectionRequest.getTo())){
                connectionServiceImpl.answerConnectionRequest(connectionRequest.getTo(),connectionRequest.getFrom(),connectionRequest.getStatus());
                return ResponseEntity.ok("Connection request " + connectionRequest.getStatus() + " successfully");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }
}