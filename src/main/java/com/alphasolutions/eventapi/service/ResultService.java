package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.exception.PalestraNotFoundException;
import com.alphasolutions.eventapi.exception.UserNotFoundException;
import com.alphasolutions.eventapi.model.entity.Palestra;
import com.alphasolutions.eventapi.model.dto.ResultDTO;
import com.alphasolutions.eventapi.model.entity.Ranking;
import com.alphasolutions.eventapi.model.entity.Results;
import com.alphasolutions.eventapi.model.entity.User;
import com.alphasolutions.eventapi.repository.RankingRepository;
import com.alphasolutions.eventapi.repository.ResultsRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ResultService {

    private static final Logger logger = LoggerFactory.getLogger(ResultService.class);

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

    public Results iniciarQuiz(String userId, Long idPalestra) {
        logger.info("Iniciando quiz para userId: {}, idPalestra: {}", userId, idPalestra);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Usuário não encontrado: {}", userId);
                    return new UserNotFoundException("User " + userId + " not found");
                });

        Palestra palestra = palestraService.findPalestraById(idPalestra);
        if (palestra == null) {
            logger.error("Palestra não encontrada: {}", idPalestra);
            throw new PalestraNotFoundException("Palestra " + idPalestra + " not found");
        }

        // Overwrite any existing unfinished quiz
        Optional<Results> existingResultOpt = resultsRepository.findLatestUnfinishedByUserIdAndPalestraId(userId, idPalestra);
        if (existingResultOpt.isPresent()) {
            logger.warn("Sobrescrevendo quiz inacabado para userId: {}, idPalestra: {}", userId, idPalestra);
            Results existingResult = existingResultOpt.get();
            resultsRepository.delete(existingResult);
        }

        Results result = new Results(0, 0, 0, user, palestra);
        result.setStartTime(LocalDateTime.now());
        Results savedResult = resultsRepository.save(result);
        logger.info("Quiz iniciado com sucesso: resultId: {}", savedResult.getIdResult());
        return savedResult;
    }

    public Results finalizarQuiz(String userId, Long idPalestra, ResultDTO resultDTO) {
        logger.info("Finalizando quiz para userId: {}, idPalestra: {}", userId, idPalestra);

        Results existingResult = resultsRepository.findLatestUnfinishedByUserIdAndPalestraId(userId, idPalestra)
                .orElseThrow(() -> {
                    logger.error("Quiz não iniciado para userId: {}, idPalestra: {}", userId, idPalestra);
                    return new RuntimeException("Início não encontrado para este usuário e palestra");
                });

        existingResult.setCorrectAnswers(resultDTO.getCorrectAnswers());
        existingResult.setWrongAnswers(resultDTO.getWrongAnswers());
        existingResult.setScore(resultDTO.getScore());
        existingResult.setEndTime(LocalDateTime.now());

        Duration duration = Duration.between(existingResult.getStartTime(), existingResult.getEndTime());
        existingResult.setTotalTime(duration.toSeconds() + duration.toMillisPart() / 1000.0);

        Results updatedResult = resultsRepository.save(existingResult);
        logger.info("Quiz finalizado com sucesso: resultId: {}, score: {}", updatedResult.getIdResult(), updatedResult.getScore());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Usuário não encontrado: {}", userId);
                    return new UserNotFoundException("User " + userId + " not found");
                });
        updateRanking(user, resultDTO.getCorrectAnswers());

        return updatedResult;
    }

    private void updateRanking(User user, int correctAnswers) {
        logger.debug("Atualizando ranking para userId: {}, correctAnswers: {}", user.getIdUser(), correctAnswers);
        Ranking actualRanking = rankingRepository.findRankingByUser(user);
        if (actualRanking == null) {
            actualRanking = new Ranking();
            actualRanking.setUser(user);
            actualRanking.setAcertos(correctAnswers);
        } else {
            actualRanking.setAcertos(actualRanking.getAcertos() + correctAnswers);
        }
        rankingRepository.save(actualRanking);
        logger.debug("Ranking atualizado: acertos: {}", actualRanking.getAcertos());
    }

    public Optional<Results> findResultByUserAndPalestra(User user, Palestra palestra) {
        return resultsRepository.findByUserAndPalestra(user, palestra);
    }
}