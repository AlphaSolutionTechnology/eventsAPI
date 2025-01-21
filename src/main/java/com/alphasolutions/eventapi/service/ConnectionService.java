package com.alphasolutions.eventapi.service;

public interface ConnectionService {

    boolean isConnected(Long idSolicitante, Long idSolicitado);

    void connect(Long idSolicitante, Long idSolicitado);
}
