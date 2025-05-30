package com.alphasolutions.eventapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventSubscriptionPojo {
    private Long eventId;
    private String userId;
}
