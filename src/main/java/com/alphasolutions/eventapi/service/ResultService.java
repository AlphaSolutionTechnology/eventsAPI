package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.exception.PalestraNotFoundException;
import com.alphasolutions.eventapi.exception.UserAlreadyExistsException;
import com.alphasolutions.eventapi.exception.UserNotFoundException;
import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.model.Ranking;
import com.alphasolutions.eventapi.model.ResultDTO;
import com.alphasolutions.eventapi.model.Results;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.repository.RankingRepository;
import com.alphasolutions.eventapi.repository.ResultsRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User " + userId + " not found"));

        Palestra palestra = palestraService.findPalestraById(idPalestra);
        if (palestra == null) {
            throw new RuntimeException("Palestra " + idPalestra + " not found");
        }

        try {
            Optional<Results> existingResultOpt = resultsRepository.findLatestUnfinishedByUserIdAndPalestraId(userId, idPalestra);

            if (existingResultOpt.isPresent()) {
                Results existingResult = existingResultOpt.get();
                existingResult.setCorrectAnswers(result.getCorrectAnswerCount());
                existingResult.setWrongAnswers(result.getWrongAnswerCount());
                existingResult.setScore(result.getScore());
                existingResult.setEndTime(LocalDateTime.now());

                Duration duration = Duration.between(existingResult.getStartTime(), existingResult.getEndTime());
                existingResult.setTotalTime(duration.toSeconds() + duration.toMillisPart() / 1000.0);

                resultsRepository.save(existingResult);

                updateRanking(user, result.getCorrectAnswerCount());
            } else {
                Results newResult = new Results(
                        result.getCorrectAnswerCount(),
                        result.getScore(),
                        result.getWrongAnswerCount(),
                        user,
                        palestra
                );
                newResult.setEndTime(LocalDateTime.now());
                Duration duration = Duration.between(newResult.getStartTime(), newResult.getEndTime());
                newResult.setTotalTime(duration.toSeconds() + duration.toMillisPart() / 1000.0);

                resultsRepository.save(newResult);

                updateRanking(user, result.getCorrectAnswerCount());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar resultado: " + e.getMessage(), e);
        }
    }

    private void updateRanking(User user, int correctAnswers) {
        Ranking actualRanking = rankingRepository.findRankingByUser(user);
        if (actualRanking == null) {
            actualRanking = new Ranking();
            actualRanking.setUser(user);
            actualRanking.setAcertos(correctAnswers);
        } else {
            actualRanking.setAcertos(actualRanking.getAcertos() + correctAnswers);
        }
        rankingRepository.save(actualRanking);
    }

    public Optional<Results> findResultByUserAndPalestra(User user, Palestra palestra) {
        return resultsRepository.findByUserAndPalestra(user, palestra);
    }

    public Results iniciarQuiz(String userId, Long idPalestra) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User " + userId + " not found"));

        Palestra palestra = palestraService.findPalestraById(idPalestra);
        if (palestra == null) {
            throw new PalestraNotFoundException("Palestra " + idPalestra + " not found");
        }

        Optional<Results> existingResult = resultsRepository.findLatestUnfinishedByUserIdAndPalestraId(userId, idPalestra);
        if (existingResult.isPresent()) {
            throw new UserAlreadyExistsException("Quiz já iniciado para este usuário e palestra");
        }

        Results result = new Results(0, 0, 0, user, palestra);
        return resultsRepository.save(result);
    }

    public Results finalizarQuiz(String userId, Long idPalestra, ResultDTO result) {
        Results existingResult = resultsRepository.findLatestUnfinishedByUserIdAndPalestraId(userId, idPalestra)
                .orElseThrow(() -> new RuntimeException("Início não encontrado para este usuário e palestra"));

        existingResult.setCorrectAnswers(result.getCorrectAnswerCount());
        existingResult.setWrongAnswers(result.getWrongAnswerCount());
        existingResult.setScore(result.getScore());
        existingResult.setEndTime(LocalDateTime.now());

        Duration duration = Duration.between(existingResult.getStartTime(), existingResult.getEndTime());
        existingResult.setTotalTime(duration.toSeconds() + duration.toMillisPart() / 1000.0);

        Results updatedResult = resultsRepository.save(existingResult);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User " + userId + " not found"));
        updateRanking(user, result.getCorrectAnswerCount());

        return updatedResult;
    }
}