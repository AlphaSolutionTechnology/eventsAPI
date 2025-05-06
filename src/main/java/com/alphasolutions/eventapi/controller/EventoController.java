package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.exception.AlreadySubscribedInThisEventException;
import com.alphasolutions.eventapi.exception.InvalidEventDateException;
import com.alphasolutions.eventapi.exception.InvalidRoleException;
import com.alphasolutions.eventapi.exception.InvalidTokenException;
import com.alphasolutions.eventapi.model.dto.EventDTO;
import com.alphasolutions.eventapi.model.dto.EventSubscriptionPojo;
import com.alphasolutions.eventapi.service.AuthService;
import com.alphasolutions.eventapi.service.EventService;
import com.alphasolutions.eventapi.service.UserService;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(("/api/event"))
public class EventoController {

    private final AuthService authService;
    private final EventService eventService;

    public EventoController(AuthService authService, EventService eventService) {
        this.authService = authService;
        this.eventService = eventService;

    }

    @PatchMapping("/subscribe")
    public ResponseEntity<?> subscribe(@CookieValue("eventToken") String eventToken ,@RequestBody EventSubscriptionPojo eventSubscriptionForm) {
        try {
            authService.authenticate(eventToken);
            eventService.subscribe(eventSubscriptionForm);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (AlreadySubscribedInThisEventException alreadySubscribedInThisEventException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(alreadySubscribedInThisEventException.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @GetMapping("/event-list")
    public ResponseEntity<?> eventList(@CookieValue("eventToken") String eventToken) {
        try {
            authService.authenticate(eventToken);
            return ResponseEntity.ok(eventService.getEvents());
        }catch (InvalidTokenException invalidTokenException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(invalidTokenException.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/participants/{eventId}")
    public ResponseEntity<?> participants(@CookieValue("eventToken") String eventToken, @PathVariable(value = "eventId") Long idEvento) {
        try {
            authService.authenticate(eventToken);
            return ResponseEntity.ok(eventService.getParticipants(idEvento));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody EventDTO eventDTO, @CookieValue("eventToken") String eventToken ){
        try {
            authService.authenticateAdmin(eventToken);
            eventService.createEvent(eventDTO);
            return ResponseEntity.status(HttpStatus.CREATED). build();
        }catch (InvalidTokenException | InvalidRoleException invalidTokenException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(invalidTokenException.getMessage());
        }catch (InvalidEventDateException | IllegalArgumentException invalidEventDateException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(invalidEventDateException.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }
}
