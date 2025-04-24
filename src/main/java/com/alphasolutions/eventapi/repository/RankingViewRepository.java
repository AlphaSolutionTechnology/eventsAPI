package com.alphasolutions.eventapi.repository;

import com.alphasolutions.eventapi.model.entity.RankingView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RankingViewRepository extends JpaRepository<RankingView, Long> {
    List<RankingView> findByIdEvento(Long idEvento);
}
