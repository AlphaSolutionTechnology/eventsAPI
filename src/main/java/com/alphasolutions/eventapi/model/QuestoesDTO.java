// File: QuestoesDTO.java
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
    private Long idQuestao;
    private String idUser;
    private String enunciado;
    private List<String> choices;
    private Long idPalestra;
    private String correctAnswer;
}
