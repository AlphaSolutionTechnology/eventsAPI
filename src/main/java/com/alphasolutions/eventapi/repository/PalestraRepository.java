package com.alphasolutions.eventapi.repository;

import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.model.User;

import jakarta.transaction.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PalestraRepository extends JpaRepository<Palestra, Long> {
    boolean existsByUniqueCode(String uniqueCode);
    Optional<Palestra> findByUniqueCode(String uniqueCode);
    void removePalestraById(Long id);
    List<Palestra> findAllByUser(User user);
    
    
    @Modifying
    @Transactional
    @Query("UPDATE Palestra p SET p.quizzLiberado = true WHERE p.id = :palestraId")
    void atualizarQuizzLiberado(Long palestraId);

    @Modifying
    @Transactional
    @Query("UPDATE Palestra p SET p.horaLiberacao = :hora WHERE p.id = :palestraId")
    void atualizarHoraLiberacao(Long palestraId, Timestamp hora);

    @Modifying
    @Transactional
    @Query("UPDATE Palestra p SET p.quizzLiberado = true WHERE p.horaLiberacao <= CURRENT_TIMESTAMP AND p.quizzLiberado = false")
    int liberarQuizzNaHoraConfigurada();
    

}
