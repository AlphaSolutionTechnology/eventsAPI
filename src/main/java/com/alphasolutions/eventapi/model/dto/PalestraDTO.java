package com.alphasolutions.eventapi.model.dto;

import com.alphasolutions.eventapi.model.entity.Evento;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.sql.Timestamp;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp time;
    private boolean unlockedQuizz;
    private String uniqueCode;
    private String description;
}
