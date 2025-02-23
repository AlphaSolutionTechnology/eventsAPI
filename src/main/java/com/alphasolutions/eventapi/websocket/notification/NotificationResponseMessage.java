package com.alphasolutions.eventapi.websocket.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class NotificationResponseMessage {
    private String message;
    private String to;
    public NotificationResponseMessage(String message) {
        this.message = message;
        this.to = "";
    }
}
