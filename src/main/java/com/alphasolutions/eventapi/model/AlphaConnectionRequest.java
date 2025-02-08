package com.alphasolutions.eventapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AlphaConnectionRequest {
    private String to;
    private String from;
    private String status;
}
