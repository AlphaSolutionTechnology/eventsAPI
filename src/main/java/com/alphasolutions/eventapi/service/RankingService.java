package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.model.Ranking;
import com.alphasolutions.eventapi.model.RankingView;
import com.alphasolutions.eventapi.repository.RankingRepository;
import com.alphasolutions.eventapi.repository.RankingViewRepository;
import org.springframework.stereotype.Service;
import com.alphasolutions.eventapi.model.User;

import java.util.List;

@Service
public class RankingService {
    private final RankingViewRepository rankingViewRepository;
    private final RankingRepository rankingRepository;

    public RankingService(RankingViewRepository rankingViewRepository, RankingRepository rankingRepository) {
        this.rankingViewRepository = rankingViewRepository;
        this.rankingRepository = rankingRepository;
    }

    public List<RankingView> getAllUserInRanking() {
        return rankingViewRepository.findAllByEventoId(1L);
    }

     public void inscreverUsuarioNoRanking(Palestra palestra, User user) {
        if (!rankingRepository.existsByPalestraAndUser(palestra, user)) {
            Ranking novoRanking = new Ranking(palestra, user);
            rankingRepository.save(novoRanking);
        }
    }

    public List<RankingView> getRankingByPalestra(Long idPalestra) {
        return rankingViewRepository.findAllByPalestraId(idPalestra);
    }
    
}
