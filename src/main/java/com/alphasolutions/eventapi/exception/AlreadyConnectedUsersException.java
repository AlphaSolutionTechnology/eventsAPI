package com.alphasolutions.eventapi.exception;

public class AlreadyConnectedUsersException extends RuntimeException {
    public AlreadyConnectedUsersException(String message) {
        super(message);
    }
}
