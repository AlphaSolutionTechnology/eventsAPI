package com.alphasolutions.eventapi.repository;

import com.alphasolutions.eventapi.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    <Optional> Evento findById(Long id);
    <Optional> Evento findByNome(String nome);
    
}
