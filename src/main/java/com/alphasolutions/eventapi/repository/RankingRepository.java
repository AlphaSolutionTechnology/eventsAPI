package com.alphasolutions.eventapi.repository;

import com.alphasolutions.eventapi.model.Ranking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {
}
