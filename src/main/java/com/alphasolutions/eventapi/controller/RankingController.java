package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.model.entity.RankingView;
import com.alphasolutions.eventapi.service.RankingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


//teste

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingService rankingService;
    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/getupdatedranking")
    public List<RankingView> getUpdateRanking() {
        return rankingService.getAllUserInRanking();
    }

    // @GetMapping("/{idPalestra}")
    //  public List<RankingView> getRankingByPalestra(@PathVariable Long idPalestra) {
    //     return rankingService.getRankingByPalestra(idPalestra);
    // }

}
