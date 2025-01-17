package com.alphasolutions.eventapi.model.repository;

import com.alphasolutions.eventapi.model.entities.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoDAO extends JpaRepository<Evento, Integer> {

}
