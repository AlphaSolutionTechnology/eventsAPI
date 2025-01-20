package com.alphasolutions.eventapi.service;

public interface ConnectionService {
    boolean isConnected(String idSolicitante, String idSolicitado);
    void connect(String idSolicitante, String idSolicitado);
}
