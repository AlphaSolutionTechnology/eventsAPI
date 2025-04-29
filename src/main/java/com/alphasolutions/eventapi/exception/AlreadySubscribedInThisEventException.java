package com.alphasolutions.eventapi.exception;

public class AlreadySubscribedInThisEventException extends RuntimeException {
    public AlreadySubscribedInThisEventException(String message) {
        super(message);
    }
}
