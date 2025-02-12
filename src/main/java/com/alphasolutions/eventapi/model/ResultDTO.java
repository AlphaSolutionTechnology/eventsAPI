package com.alphasolutions.eventapi.model;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultDTO {
    int correctAnswerCount;
    int wrongAnswerCount;
    int score;
    Double totalTime;
}
