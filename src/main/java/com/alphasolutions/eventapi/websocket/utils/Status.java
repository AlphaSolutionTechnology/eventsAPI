package com.alphasolutions.eventapi.websocket.utils;

import lombok.Getter;

@Getter
public enum Status {
    ACCEPTED("ACCEPTED"), DECLINED("DECLINED"), WAITING("WAITING");

    private final String status;

    Status(String status) {
        this.status = status;
    }

}
