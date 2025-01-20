package com.alphasolutions.eventapi.repository;

import com.alphasolutions.eventapi.model.Ranking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Ranking r SET r.conexoes = r.conexoes + 1 WHERE r.user.id = :userId")
    void incrementConnection(@Param("userId") String userId);
}
