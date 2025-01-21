package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.User;

public interface ConnectionService {

    boolean isConnected(User Solicitante, User idSolicitado);

    void connect(Long idSolicitante, Long idSolicitado);
}
