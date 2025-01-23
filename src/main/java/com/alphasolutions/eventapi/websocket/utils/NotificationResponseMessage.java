package com.alphasolutions.eventapi.websocket.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;

@Getter
@Setter
@AllArgsConstructor
public class NotificationResponseMessage {
    private String message;
}
