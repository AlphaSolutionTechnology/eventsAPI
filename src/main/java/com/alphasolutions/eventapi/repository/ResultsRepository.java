package com.alphasolutions.eventapi.repository;

import com.alphasolutions.eventapi.model.Results;
import com.alphasolutions.eventapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResultsRepository extends JpaRepository<Results, Long> {
    Optional<Results> findFirstByUser(User user);
}
