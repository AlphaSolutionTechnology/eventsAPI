package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.model.ResultDTO;
import com.alphasolutions.eventapi.model.Results;
import com.alphasolutions.eventapi.model.User;
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

    public boolean saveResult(ResultDTO result, String userId, Long idPalestra) {
        User user = userRepository.findById(userId).orElse(null);
        Palestra palestra = palestraService.findPalestraById(idPalestra);
        if(user == null) {
            return  false;
        }
        try {
            Results results = resultsRepository.findByUserAndPalestra(user,palestra).orElse(null);
            if(results != null) {
                resultsRepository.save(new Results(results.getIdResult(),result.getCorrectAnswerCount(),result.getScore(),result.getWrongAnswerCount(),result.getTotalTime(),user, palestra));
                return true;
            }
            resultsRepository.save(new Results(result.getCorrectAnswerCount(),result.getScore(),result.getWrongAnswerCount(),result.getTotalTime(),user, palestra));
            rankingRepository.incrementAcertos(user.getId(), result.getCorrectAnswerCount());
            return true;
        } catch (Exception e) {
            return false;
        }

    }


    public Optional<Results> findResultByUserAndPalestra(User user, Palestra palestra){
        return resultsRepository.findByUserAndPalestra(user, palestra);
    }


}
