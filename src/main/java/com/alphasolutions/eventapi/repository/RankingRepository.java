package com.alphasolutions.eventapi.repository;

import com.alphasolutions.eventapi.model.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Ranking r SET r.conexoes = r.conexoes + 1 WHERE r.user.idUser = :userId")
    void incrementConnection(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query("UPDATE Ranking r SET r.acertos = :acertos + r.acertos WHERE r.user.idUser = :userId")
    void incrementAcertos(@Param("userId") String userId, @Param("acertos") Integer acertos);

    @Query("SELECT r.conexoes FROM Ranking r WHERE r.user.idUser = :userId")
    int findConexoesByUserId(String userId);

    boolean existsByUser(User user);
    Ranking findByPalestraAndUser(Palestra palestra, User user);

    Ranking findRankingByUser(User user);

    List<RankingView> findAllByEvento(Evento evento);
}
