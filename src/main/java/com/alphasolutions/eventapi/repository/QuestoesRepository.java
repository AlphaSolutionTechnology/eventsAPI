package com.alphasolutions.eventapi.repository;

import com.alphasolutions.eventapi.model.entity.Questoes;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestoesRepository extends JpaRepository<Questoes, Long> {

    List<Questoes> findByIdPalestra(Long idPalestra);
    Optional<Questoes> findById(Long id);
}
