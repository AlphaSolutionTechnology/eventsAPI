package com.alphasolutions.eventapi.exception;

public class WaitingForResponseException extends RuntimeException {
    public WaitingForResponseException(String s) {
        super(s);
    }
}
