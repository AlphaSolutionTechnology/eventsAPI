package com.alphasolutions.eventapi.controller;

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
