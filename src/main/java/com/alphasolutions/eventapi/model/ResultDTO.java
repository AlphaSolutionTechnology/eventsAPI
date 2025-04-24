
package com.alphasolutions.eventapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultDTO {
    private int correctAnswerCount;
    private int wrongAnswerCount;
    private int score;
    private Double totalTime;
}
