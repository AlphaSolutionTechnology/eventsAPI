package com.alphasolutions.eventapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestoesDTO {
    Long idQuestao;
    String idUser;
    String enunciado;
    List<String> choices;
    Long idPalestra;
    String correctAnswer;
}
