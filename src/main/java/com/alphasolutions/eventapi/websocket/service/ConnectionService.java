package com.alphasolutions.eventapi.websocket.service;

import com.alphasolutions.eventapi.model.Conexao;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.model.UserConnetionDTO;
import com.alphasolutions.eventapi.websocket.notification.NotificationResponseMessage;
import com.alphasolutions.eventapi.websocket.notification.Status;

import java.util.List;

public interface ConnectionService {

    boolean isConnected(User Solicitante, User idSolicitado);

    NotificationResponseMessage connect(String idSolicitante, String idSolicitado, Status status);

    List<Conexao> getPendingConnections(String token);

    void answerConnectionRequest(String to, String from, String status);

    List<UserConnetionDTO> getAcceotedConnections(String token);
}
