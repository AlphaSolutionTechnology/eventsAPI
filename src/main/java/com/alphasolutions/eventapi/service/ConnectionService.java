package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.websocket.notification.NotificationResponseMessage;
import com.alphasolutions.eventapi.websocket.notification.Status;

public interface ConnectionService {

    boolean isConnected(User Solicitante, User idSolicitado);

    NotificationResponseMessage connect(String idSolicitante, String idSolicitado, Status status);
}
