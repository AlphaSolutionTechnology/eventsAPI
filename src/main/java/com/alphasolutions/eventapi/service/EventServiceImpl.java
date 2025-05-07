package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.exception.AlreadySubscribedInThisEventException;
import com.alphasolutions.eventapi.exception.InvalidEventDateException;
import com.alphasolutions.eventapi.exception.UserNotFoundException;
import com.alphasolutions.eventapi.model.dto.EventDTO;
import com.alphasolutions.eventapi.model.dto.EventSubscriptionPojo;
import com.alphasolutions.eventapi.model.dto.UserData;
import com.alphasolutions.eventapi.model.entity.Evento;
import com.alphasolutions.eventapi.model.entity.User;
import com.alphasolutions.eventapi.repository.EventoRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
import jakarta.persistence.Id;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventoRepository eventoRepository;
    private final UserRepository userRepository;


    public EventServiceImpl(EventoRepository eventoRepository, UserRepository userRepository) {
        this.eventoRepository = eventoRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Evento> getEvents() {
        return eventoRepository.findAll();
    }

    @Override
    public List<UserData> getParticipants(Long idEvento) {
        Evento evento = eventoRepository.findByIdEvento(idEvento);
        if (idEvento == null || idEvento == 0) {
            throw new IllegalArgumentException("IdEvento should not be null");
        }
        if (evento == null) {
            throw new RuntimeException("NO EVENT FOUND");
        }

        List<UserData> listOfUserData = new ArrayList<>();
        for(User user : userRepository.findAllByEvento(evento)) {
            if(!user.getRole().getRole().equals("Administrador")) {
                listOfUserData.add(new UserData(user.getIdUser(),user.getNome(), user.getEmail(), user.getRole().getRole(),user.getUniqueCode()));
            }
        }
        System.out.println(listOfUserData);
        return listOfUserData;
    }

    @Override
    public void createEvent(EventDTO evento) {
        if(evento.getName() == null || evento.getName().isEmpty()) {
            throw new IllegalArgumentException("Name should not be empty");
        }
        if(evento.getEventDate() != null  && evento.getEventDate().isBefore(LocalDate.now())) {
            throw new InvalidEventDateException("Data do evento não pode ser antes do dia atual!");
        }
        Evento event = new Evento();
        event.setNome(evento.getName());
        event.setDescricao(evento.getDescription());
        event.setDataEvento(evento.getEventDate());
        event.setLocalizacao(evento.getLocation());
        event.setImagemUrl(evento.getImageUrl());
        eventoRepository.save(event);
    }

    @Override
    public void subscribe(EventSubscriptionPojo eventSubscriptionForm) {
        Long eventId = eventSubscriptionForm.getEventId();
        String userId = eventSubscriptionForm.getUserId();
        Evento evento = eventoRepository.findByIdEvento(eventId);
        User actualUser = userRepository.findById(userId).orElse(null);
        if (eventId == null || eventId == 0 || evento == null) {
            throw new RuntimeException("Event not found");
        }if (userId.isEmpty() || actualUser == null) {
            throw new UserNotFoundException("User not found");
        }if (actualUser.getEvento() != null && actualUser.getEvento().equals(evento) ) {
            throw new AlreadySubscribedInThisEventException("User is already subscribed to this event");
        }

        actualUser.setEvento(evento);
        userRepository.save(actualUser);
    }
}
