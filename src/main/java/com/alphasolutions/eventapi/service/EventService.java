package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.dto.EventSubscriptionPojo;
import com.alphasolutions.eventapi.model.dto.UserData;
import com.alphasolutions.eventapi.model.entity.Evento;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventService {

    List<Evento> getEvents();
    List<UserData> getParticipants(Long idEvento);

    void subscribe(EventSubscriptionPojo eventSubscriptionForm);
}
