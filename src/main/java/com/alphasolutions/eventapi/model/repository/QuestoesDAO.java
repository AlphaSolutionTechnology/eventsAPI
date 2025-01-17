package com.alphasolutions.eventapi.model.repository;

import com.alphasolutions.eventapi.model.entities.Questoes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestoesDAO extends JpaRepository<Questoes, Long> {
}
