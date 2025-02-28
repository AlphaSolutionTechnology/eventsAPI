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

    /**
     * Método antigo, usado no fluxo em que o front-end envia o resultado apenas ao final.
     */
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
                Results userResultInPalestra = resultsRepository.findByUserAndPalestra(user, palestra).orElse(null);

                // Atualiza os campos do objeto existente
                results.setCorrectAnswers(result.getCorrectAnswerCount() + results.getCorrectAnswers());
                results.setWrongAnswers(result.getWrongAnswerCount() + results.getWrongAnswers());
                results.setTotalTime((result.getTotalTime() + results.getTotalTime()) / 2);
                resultsRepository.save(results);

                // Atualiza ranking se for a primeira vez que esse usuário responde a esta palestra
                if (userResultInPalestra == null && actualRanking != null) {
                    actualRanking.setAcertos(actualRanking.getAcertos() + result.getCorrectAnswerCount());
                    rankingRepository.save(actualRanking);
                }
                return;
            }

            // Cria um novo registro de resultados se não existir
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

    /**
     * Novo método para atualizar ou criar o registro de resultado a cada questão respondida.
     * Chamado pelo endpoint /validateAndRecord no controller.
     */
    public void updateResult(String userId, Long idPalestra, boolean isCorrect, double timeSpent) {
        // 1. Busca usuário e palestra
        User user = userRepository.findById(userId).orElse(null);
        Palestra palestra = palestraService.findPalestraById(idPalestra);

        if (user == null) {
            throw new UserAlreadyExistsException("Usuário não encontrado: " + userId);
        }
        if (palestra == null) {
            throw new RuntimeException("Palestra não encontrada: " + idPalestra);
        }

        try {
            // 2. Verifica se já existe um registro de resultado específico para (user, palestra)
            Results userResultInPalestra = resultsRepository.findByUserAndPalestra(user, palestra).orElse(null);

            // Também podemos atualizar o ranking
            Ranking actualRanking = rankingRepository.findRankingByUser(user);

            if (userResultInPalestra == null) {
                // 3a. Se não existir, cria um novo
                userResultInPalestra = new Results();
                userResultInPalestra.setUser(user);
                userResultInPalestra.setPalestra(palestra);

                // Se acertou
                if (isCorrect) {
                    userResultInPalestra.setCorrectAnswers(1);
                    userResultInPalestra.setWrongAnswers(0);
                    userResultInPalestra.setScore(5); // Exemplo de pontuação ao acertar
                } else {
                    userResultInPalestra.setCorrectAnswers(0);
                    userResultInPalestra.setWrongAnswers(1);
                    userResultInPalestra.setScore(0);
                }
                userResultInPalestra.setTotalTime(timeSpent);

                resultsRepository.save(userResultInPalestra);

                // Atualiza ranking
                if (actualRanking != null && isCorrect) {
                    actualRanking.setAcertos(actualRanking.getAcertos() + 1);
                    rankingRepository.save(actualRanking);
                }
            } else {
                // 3b. Se existir, atualiza o registro
                if (isCorrect) {
                    userResultInPalestra.setCorrectAnswers(userResultInPalestra.getCorrectAnswers() + 1);
                    userResultInPalestra.setScore(userResultInPalestra.getScore() + 5); // Exemplo
                    if (actualRanking != null) {
                        actualRanking.setAcertos(actualRanking.getAcertos() + 1);
                    }
                } else {
                    userResultInPalestra.setWrongAnswers(userResultInPalestra.getWrongAnswers() + 1);
                }

                // Soma o tempo gasto nesta questão
                userResultInPalestra.setTotalTime(userResultInPalestra.getTotalTime() + timeSpent);

                resultsRepository.save(userResultInPalestra);

                // Salva ranking se foi atualizado
                if (actualRanking != null) {
                    rankingRepository.save(actualRanking);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retorna o registro de resultado para um determinado usuário e palestra, se existir.
     */
    public Optional<Results> findResultByUserAndPalestra(User user, Palestra palestra) {
        return resultsRepository.findByUserAndPalestra(user, palestra);
    }
}
