package com.alphasolutions.eventapi.model.dto;

import java.util.List;

public class QuestoesPublicDTO {
    private Long id;
    private String enunciado;
    private List<String> choices;
    private Long idPalestra;

    public QuestoesPublicDTO(Long id, String enunciado, List<String> choices, Long idPalestra) {
        this.id = id;
        this.enunciado = enunciado;
        this.choices = choices;
        this.idPalestra = idPalestra;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEnunciado() {
        return enunciado;
    }
    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }
    public List<String> getChoices() {
        return choices;
    }
    public void setChoices(List<String> choices) {
        this.choices = choices;
    }
    public Long getIdPalestra() {
        return idPalestra;
    }
    public void setIdPalestra(Long idPalestra) {
        this.idPalestra = idPalestra;
    }
}
