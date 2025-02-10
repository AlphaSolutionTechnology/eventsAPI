package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.RankingView;
import com.alphasolutions.eventapi.repository.RankingRepository;
import com.alphasolutions.eventapi.repository.RankingViewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankingService {
    private final RankingViewRepository rankingViewRepository;

    public RankingService(RankingViewRepository rankingViewRepository) {
        this.rankingViewRepository = rankingViewRepository;
    }

    public List<RankingView> getAllUserInRanking() {
        return rankingViewRepository.findAllByEventoId(1L);
    }
}
