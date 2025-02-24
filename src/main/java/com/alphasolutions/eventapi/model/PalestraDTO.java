package com.alphasolutions.eventapi.model;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PalestraDTO {
    private Long id;
    private String tema;
    private Evento evento;
    private String uniqueCode;
}
