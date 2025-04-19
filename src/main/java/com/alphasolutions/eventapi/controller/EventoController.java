package com.alphasolutions.eventapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alphasolutions.eventapi.exception.InvalidTokenException;
import com.alphasolutions.eventapi.model.Evento;
import com.alphasolutions.eventapi.service.AuthService;
import com.alphasolutions.eventapi.service.EventoService;

@RestController
@RequestMapping("/api/evento")
public class EventoController {

    private final EventoService eventoService;
    private final AuthService authService;


    public EventoController(EventoService eventoService, AuthService authService) {
        this.eventoService = eventoService;
        this.authService = authService;
    }


   public ResponseEntity<?> criarEvento(@CookieValue(value = "eventToken") String eventToken, @RequestBody Evento evento) {
        try {
            // Authenticate the user using the provided token
            authService.authenticate(eventToken);

            // Create the event using the service
            eventoService.createEvent(
                evento.getNome(),
                evento.getDataEvento(),
                evento.getLocal(),
                evento.getDescricao(),
                evento.getImagemUrl()
            );

            return ResponseEntity.ok("Evento criado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
       

   }


}
