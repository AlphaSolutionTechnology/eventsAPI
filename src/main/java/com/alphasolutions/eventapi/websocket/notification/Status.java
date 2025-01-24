package com.alphasolutions.eventapi.websocket.notification;

import lombok.Getter;

@Getter
public enum Status {
    ACCEPTED("ACCEPTED"), DECLINED("DECLINED"), WAITING("WAITING");

    private final String status;

    Status(String status) {
        this.status = status;
    }

}
