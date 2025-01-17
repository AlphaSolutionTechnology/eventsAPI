package com.alphasolutions.eventapi.model.repository;

import com.alphasolutions.eventapi.model.entities.Ranking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankingDAO extends JpaRepository<Ranking, Long> {
}
