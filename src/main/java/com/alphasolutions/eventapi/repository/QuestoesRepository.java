package com.alphasolutions.eventapi.repository;

import com.alphasolutions.eventapi.model.Questoes;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestoesRepository extends JpaRepository<Questoes, Long> {

    List<Questoes> findByIdPalestra(Long idPalestra);
}
