package com.alphasolutions.eventapi.repository;

import com.alphasolutions.eventapi.model.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Integer> {

    Evento findByIdEvento(Long idEvento);
}
