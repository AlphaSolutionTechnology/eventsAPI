package com.alphasolutions.eventapi.websocket.service;

import com.alphasolutions.eventapi.websocket.notification.NotificationResponseMessage;
import com.alphasolutions.eventapi.websocket.notification.Status;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final ConnectionServiceImpl connectionServiceImpl;

    public NotificationService(ConnectionServiceImpl connectionServiceImpl) {
        this.connectionServiceImpl = connectionServiceImpl;
    }

    public void askForConnection(String currentUserUniqueCode, String toUserUniqueCode) {
        connectionServiceImpl.connect(currentUserUniqueCode, toUserUniqueCode, Status.WAITING);
    }

    public NotificationResponseMessage acceptConnection(String currentUserUniqueCode) {
        //TODO
        return null;
    }
}
