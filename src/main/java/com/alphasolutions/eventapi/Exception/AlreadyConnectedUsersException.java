package com.alphasolutions.eventapi.Exception;

public class AlreadyConnectedUsersException extends RuntimeException {
    public AlreadyConnectedUsersException(String message) {
        super(message);
    }
}
