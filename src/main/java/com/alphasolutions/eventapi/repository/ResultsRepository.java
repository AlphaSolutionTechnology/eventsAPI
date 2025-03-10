package com.alphasolutions.eventapi.repository;

import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.model.Results;
import com.alphasolutions.eventapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ResultsRepository extends JpaRepository<Results, Long> {

    @Query("SELECT r FROM Results r WHERE r.user.id = :userId AND r.palestra.id = :palestraId AND r.endTime IS NULL ORDER BY r.startTime DESC LIMIT 1")
    Optional<Results> findLatestUnfinishedByUserIdAndPalestraId(String userId, Long palestraId);

    Optional<Results> findByUserAndPalestra(User user, Palestra palestra);
}