package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.exception.UserAlreadyExistsException;
import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.model.Ranking;
import com.alphasolutions.eventapi.model.ResultDTO;
import com.alphasolutions.eventapi.model.Results;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.repository.RankingRepository;
import com.alphasolutions.eventapi.repository.ResultsRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResultService {

    private final ResultsRepository resultsRepository;
    private final UserRepository userRepository;
    private final RankingRepository rankingRepository;
    private final PalestraService palestraService;

    public ResultService(
            ResultsRepository resultsRepository,
            UserRepository userRepository,
            RankingRepository rankingRepository,
            PalestraService palestraService
    ) {
        this.resultsRepository = resultsRepository;
        this.userRepository = userRepository;
        this.rankingRepository = rankingRepository;
        this.palestraService = palestraService;
    }

    public void saveResult(ResultDTO result, String userId, Long idPalestra) {
        User user = userRepository.findById(userId).orElse(null);
        Palestra palestra = palestraService.findPalestraById(idPalestra);
        if (user == null) {
            throw new UserAlreadyExistsException("User " + userId + " already exists");
        }
        try {
            Results results = resultsRepository.findResultsByUser(user);
            Ranking actualRanking = rankingRepository.findRankingByUser(user);

            if (results != null) {
                Results userResultInPalestra = resultsRepository
                        .findByUserAndPalestra(user, palestra)
                        .orElse(null);

                results.setCorrectAnswers(result.getCorrectAnswerCount() + results.getCorrectAnswers());
                results.setWrongAnswers(result.getWrongAnswerCount() + results.getWrongAnswers());
                results.setTotalTime((result.getTotalTime() + results.getTotalTime()) / 2);
                resultsRepository.save(results);

                if (userResultInPalestra == null && actualRanking != null) {
                    actualRanking.setAcertos(actualRanking.getAcertos() + result.getCorrectAnswerCount());
                    rankingRepository.save(actualRanking);
                }
                return;
            }

            Results newResult = new Results(
                    result.getCorrectAnswerCount(),
                    result.getScore(),
                    result.getWrongAnswerCount(),
                    result.getTotalTime(),
                    user,
                    palestra
            );
            resultsRepository.save(newResult);

            if (actualRanking != null) {
                actualRanking.setAcertos(actualRanking.getAcertos() + result.getCorrectAnswerCount());
                rankingRepository.save(actualRanking);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateResult(String userId, Long idPalestra, boolean isCorrect, double timeSpent) {
        User user = userRepository.findById(userId).orElse(null);
        Palestra palestra = palestraService.findPalestraById(idPalestra);

        if (user == null) {
            throw new UserAlreadyExistsException("Usuário não encontrado: " + userId);
        }
        if (palestra == null) {
            throw new RuntimeException("Palestra não encontrada: " + idPalestra);
        }

        try {
            Results userResultInPalestra = resultsRepository.findByUserAndPalestra(user, palestra).orElse(null);
            Ranking actualRanking = rankingRepository.findRankingByUser(user);

            if (userResultInPalestra == null) {
                userResultInPalestra = new Results();
                userResultInPalestra.setUser(user);
                userResultInPalestra.setPalestra(palestra);

                if (isCorrect) {
                    userResultInPalestra.setCorrectAnswers(1);
                    userResultInPalestra.setWrongAnswers(0);
                    userResultInPalestra.setScore(5);
                } else {
                    userResultInPalestra.setCorrectAnswers(0);
                    userResultInPalestra.setWrongAnswers(1);
                    userResultInPalestra.setScore(0);
                }
                userResultInPalestra.setTotalTime(timeSpent);
                resultsRepository.save(userResultInPalestra);

                if (actualRanking != null && isCorrect) {
                    actualRanking.setAcertos(actualRanking.getAcertos() + 1);
                    rankingRepository.save(actualRanking);
                }
            } else {
                if (isCorrect) {
                    userResultInPalestra.setCorrectAnswers(userResultInPalestra.getCorrectAnswers() + 1);
                    userResultInPalestra.setScore(userResultInPalestra.getScore() + 5);
                    if (actualRanking != null) {
                        actualRanking.setAcertos(actualRanking.getAcertos() + 1);
                    }
                } else {
                    userResultInPalestra.setWrongAnswers(userResultInPalestra.getWrongAnswers() + 1);
                }
                userResultInPalestra.setTotalTime(userResultInPalestra.getTotalTime() + timeSpent);
                resultsRepository.save(userResultInPalestra);

                if (actualRanking != null) {
                    rankingRepository.save(actualRanking);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setFinalTotalTime(String userId, Long idPalestra, Double totalTime) {
        User user = userRepository.findById(userId).orElse(null);
        Palestra palestra = palestraService.findPalestraById(idPalestra);
        if (user == null) {
            throw new UserAlreadyExistsException("Usuário não encontrado: " + userId);
        }
        if (palestra == null) {
            throw new RuntimeException("Palestra não encontrada: " + idPalestra);
        }
        try {
            Optional<Results> opt = resultsRepository.findByUserAndPalestra(user, palestra);
            if (opt.isPresent()) {
                Results results = opt.get();
                results.setTotalTime(totalTime);
                resultsRepository.save(results);
            } else {
                throw new RuntimeException("Resultado não encontrado para atualizar tempo final");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Results> findResultByUserAndPalestra(User user, Palestra palestra) {
        return resultsRepository.findByUserAndPalestra(user, palestra);
    }
}
