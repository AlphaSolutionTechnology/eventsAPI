package com.alphasolutions.eventapi.websocket.service;

import com.alphasolutions.eventapi.model.entity.Conexao;
import com.alphasolutions.eventapi.model.entity.User;
import com.alphasolutions.eventapi.model.dto.UserConnetionDTO;
import com.alphasolutions.eventapi.websocket.notification.Status;

import java.util.List;

public interface ConnectionService {

    boolean isConnected(User Solicitante, User idSolicitado);

    void connect(String idSolicitante, String idSolicitado, Status status);

    List<Conexao> getPendingConnections(String token);

    void answerConnectionRequest(String to, String from, String status);

    List<UserConnetionDTO> getAcceotedConnections(String token);
}
