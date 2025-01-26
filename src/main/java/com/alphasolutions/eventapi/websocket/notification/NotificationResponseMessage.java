package com.alphasolutions.eventapi.websocket.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NotificationResponseMessage {
    private String message;
    private String to;
    public NotificationResponseMessage(String message) {
        this.message = message;
        this.to = "";
    }
}
