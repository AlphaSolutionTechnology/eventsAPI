package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.websocket.utils.NotificationResponseMessage;
import com.alphasolutions.eventapi.websocket.utils.Status;

public interface ConnectionService {

    boolean isConnected(User Solicitante, User idSolicitado);

    NotificationResponseMessage connect(String idSolicitante, String idSolicitado, Status status);
}
