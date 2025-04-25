package com.alphasolutions.eventapi.service;

import java.sql.Date;

import org.springframework.stereotype.Service;

import com.alphasolutions.eventapi.model.Evento;
import com.alphasolutions.eventapi.repository.EventoRepository;


@Service
public class EventoService {

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public void createEvent(String eventName, java.util.Date eventDate, String eventLocation, String eventDescription, String imagemUrl) {

        // Create a new event object using the provided parameters
        Evento evento = new Evento();
        evento.setNome(eventName);
        evento.setDataEvento(eventDate);
        evento.setLocal(eventLocation);
        evento.setDescricao(eventDescription);
        evento.setImagemUrl(imagemUrl);

        // Save the event to the database using the repository
        eventoRepository.save(evento);


    }

}
