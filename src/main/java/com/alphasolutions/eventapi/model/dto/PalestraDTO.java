package com.alphasolutions.eventapi.model.dto;

import com.alphasolutions.eventapi.model.entity.Evento;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PalestraDTO {
    private Long id;
    private String title;
    private String speaker;
    private OffsetDateTime time;
    private boolean unlockedQuizz;
    private String uniqueCode;
    private String description;
}
