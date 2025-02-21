package com.alphasolutions.eventapi.repository;

import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.model.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PalestraRepository extends JpaRepository<Palestra, Long> {
    boolean existsByUniqueCode(String uniqueCode);
    Optional<Palestra> findByUniqueCode(String uniqueCode);
    void removePalestraById(Long id);
    List<Palestra> findAllByUser(User user);
}
