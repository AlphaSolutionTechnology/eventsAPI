package com.alphasolutions.eventapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultDTO {
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer score;
}