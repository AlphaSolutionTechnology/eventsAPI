package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.dto.UserData;
import com.alphasolutions.eventapi.model.entity.Evento;
import com.alphasolutions.eventapi.model.entity.User;
import com.alphasolutions.eventapi.repository.EventoRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
import jakarta.persistence.Id;
import org.springframework.stereotype.Service;

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
            listOfUserData.add(new UserData(user.getNome(), user.getEmail(), user.getRole().getRole(),user.getUniqueCode(),user.getAvatar().getAvatarSeed()));
        }
        return listOfUserData;
    }
}
