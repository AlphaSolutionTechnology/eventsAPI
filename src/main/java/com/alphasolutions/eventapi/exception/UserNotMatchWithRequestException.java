package com.alphasolutions.eventapi.exception;

public class UserNotMatchWithRequestException extends RuntimeException {
    public UserNotMatchWithRequestException(String message) {
        super(message);
    }
}
