package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.entity.*;
import com.alphasolutions.eventapi.repository.EventoRepository;
import com.alphasolutions.eventapi.repository.RankingRepository;
import com.alphasolutions.eventapi.repository.RankingViewRepository;
import org.springframework.stereotype.Service;

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
        return rankingViewRepository.findByIdEvento(1L);
    }

    //  public void inscreverUsuarioNoRanking(Palestra palestra, User user) {
    //     if (!rankingRepository.existsByPalestraAndUser(palestra, user)) {
    //         Ranking novoRanking = new Ranking(palestra, user);
    //         rankingRepository.save(novoRanking);
    //     }
    // }

     public void inscreverUsuarioNoRanking(Palestra palestra, User user) {
         if (!rankingRepository.existsByUser(user)) {
             Ranking novoRanking = new Ranking(palestra, user);
             rankingRepository.save(novoRanking);
         }
     }


    // public List<RankingView> getRankingByPalestra(Long idPalestra) {
    //     return rankingViewRepository.findAllByPalestraId(idPalestra);
    // }

    // public void removerUsuarioDoRanking(Palestra palestra, User user) {
    //     if (rankingRepository.existsByPalestraAndUser(palestra, user)) {
    //         Ranking ranking = rankingRepository.findByPalestraAndUser(palestra, user);
    //         rankingRepository.delete(ranking);
    //     }
    // }
    
    // public boolean isUsuarioInscritoNoRanking(Palestra palestra, User user) {
    //     return rankingRepository.existsByPalestraAndUser(palestra, user);
    // }
  
}
