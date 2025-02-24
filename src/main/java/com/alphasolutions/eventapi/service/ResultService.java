package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.exception.UserAlreadyExistsException;
import com.alphasolutions.eventapi.model.*;
import com.alphasolutions.eventapi.repository.RankingRepository;
import com.alphasolutions.eventapi.repository.ResultsRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;


@Service
public class ResultService {

    private final ResultsRepository resultsRepository;
    private final UserRepository userRepository;
    private final RankingRepository rankingRepository;
    private final PalestraService palestraService;

    public ResultService(ResultsRepository resultsRepository, UserRepository userRepository, RankingRepository rankingRepository, PalestraService palestraService) {
        this.resultsRepository = resultsRepository;
        this.userRepository = userRepository;
        this.rankingRepository = rankingRepository;
        this.palestraService = palestraService;
    }

    public void saveResult(ResultDTO result, String userId, Long idPalestra) {
        User user = userRepository.findById(userId).orElse(null);
        Palestra palestra = palestraService.findPalestraById(idPalestra);
        if(user == null) {
            throw new UserAlreadyExistsException("User " + userId + " already exists");
        }
        try {
            Results results = resultsRepository.findResultsByUser(user);
            Ranking actualRanking = rankingRepository.findRankingByUser(user);

            if(results != null) {
                Results userResultInPalestra = resultsRepository.findByUserAndPalestra(user, palestra).orElse(null);

                results.setCorrectAnswers(result.getCorrectAnswerCount() + results.getCorrectAnswers()); results.setWrongAnswers(result.getWrongAnswerCount() + results.getWrongAnswers()); results.setTotalTime((result.getTotalTime() + results.getTotalTime())/2);
                resultsRepository.save(results);
                if(userResultInPalestra == null) {
                    actualRanking.setAcertos(actualRanking.getAcertos() + result.getCorrectAnswerCount());
                    rankingRepository.save(actualRanking);
                }
                return;
            }
            resultsRepository.save(new Results(result.getCorrectAnswerCount(),result.getScore(),result.getWrongAnswerCount(),result.getTotalTime(),user, palestra));
            actualRanking.setAcertos(actualRanking.getAcertos() + result.getCorrectAnswerCount());
            rankingRepository.save(actualRanking);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public Optional<Results> findResultByUserAndPalestra(User user, Palestra palestra){
        return resultsRepository.findByUserAndPalestra(user, palestra);
    }


}
