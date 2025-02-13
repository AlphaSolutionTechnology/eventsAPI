package com.alphasolutions.eventapi.repository;

import com.alphasolutions.eventapi.model.Palestra;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PalestraRepository extends JpaRepository<Palestra, Long> {
    boolean existsByTema(String tema);
    boolean existsByUniqueCode(String uniqueCode);
    Optional<Palestra> findByUniqueCode(String uniqueCode);
}
