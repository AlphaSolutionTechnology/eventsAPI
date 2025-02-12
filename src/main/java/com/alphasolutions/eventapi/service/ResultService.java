package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.ResultDTO;
import com.alphasolutions.eventapi.model.Results;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.repository.RankingRepository;
import com.alphasolutions.eventapi.repository.ResultsRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class ResultService {

    private final ResultsRepository resultsRepository;
    private final UserRepository userRepository;
    private final RankingRepository rankingRepository;

    public ResultService(ResultsRepository resultsRepository, UserRepository userRepository, RankingRepository rankingRepository) {
        this.resultsRepository = resultsRepository;
        this.userRepository = userRepository;
        this.rankingRepository = rankingRepository;
    }

    public boolean saveResult(ResultDTO result, String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            return  false;
        }
        try {
            Results results = resultsRepository.findFirstByUser(user).orElse(null);;
            if(results != null) {
                resultsRepository.save(new Results(results.getIdResult(),result.getCorrectAnswerCount(),result.getScore(),result.getWrongAnswerCount(),result.getTotalTime(),user));
                return true;
            }
            resultsRepository.save(new Results(result.getCorrectAnswerCount(),result.getScore(),result.getWrongAnswerCount(),result.getTotalTime(),user));
            rankingRepository.incrementAcertos(user.getId(), result.getCorrectAnswerCount());

        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
