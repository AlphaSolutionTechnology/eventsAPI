package com.alphasolutions.eventapi.model;

import java.sql.Timestamp;

public class QuizzStatusResponse {
    private String message;
    private Timestamp horaLiberacao;

    public QuizzStatusResponse(String message, Timestamp horaLiberacao) {
        this.message = message;
        this.horaLiberacao = horaLiberacao;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getHoraLiberacao() {
        return horaLiberacao;
    }

    public void setHoraLiberacao(Timestamp horaLiberacao) {
        this.horaLiberacao = horaLiberacao;
    }
}
