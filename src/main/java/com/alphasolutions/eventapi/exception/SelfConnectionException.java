package com.alphasolutions.eventapi.exception;

public class SelfConnectionException extends RuntimeException {
    public SelfConnectionException(String message) {
        super(message);
    }
}
