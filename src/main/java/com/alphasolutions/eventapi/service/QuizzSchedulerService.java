package com.alphasolutions.eventapi.service;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

import com.alphasolutions.eventapi.repository.PalestraRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class QuizzSchedulerService {


    private PalestraRepository palestraRepository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public QuizzSchedulerService(PalestraRepository palestraRepository){
        this.palestraRepository = palestraRepository;
    }



    @Transactional
    public void liberarOuAgendarQuizz(Long palestraId, Timestamp horaLiberacao) {
    if (horaLiberacao != null) {
        // Agendar liberação futura
        palestraRepository.atualizarHoraLiberacao(palestraId, horaLiberacao);
        agendarLiberacaoQuizz(palestraId, horaLiberacao);
    } else {
        // Liberar imediatamente
        palestraRepository.atualizarQuizzLiberado(palestraId);
    }
    }




    public void agendarLiberacaoQuizz(Long idPalestra, Timestamp horaLiberacao) {
        long delay = calcularDelay(horaLiberacao);

        scheduler.schedule(() -> {
            System.out.println("Liberando quiz para a palestra " + idPalestra);
            liberarOuAgendarQuizz(idPalestra, null);  // Força liberação imediata
        }, delay, TimeUnit.SECONDS);
    }




    private long calcularDelay(Timestamp horaLiberacao) {
        LocalDateTime horaConvertida = horaLiberacao.toLocalDateTime();
        LocalDateTime agora = LocalDateTime.now();
        return java.time.Duration.between(agora, horaConvertida).getSeconds();
    }
    

    @PostConstruct
    public void iniciarAgendamentos() {
        // Carrega as palestras pendentes e agenda a liberação
        System.out.println("iniciando agendamentos.");
        var palestras = palestraRepository.findAllByQuizzLiberadoFalse();
        for (var palestra : palestras) {
            if(palestra.getHoraLiberacao() != null){
                agendarLiberacaoQuizz(palestra.getId(), palestra.getHoraLiberacao());
            }
        }
    }
}
