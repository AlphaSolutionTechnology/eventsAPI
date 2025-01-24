package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.Conexao;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.repository.ConexaoRepository;
import com.alphasolutions.eventapi.repository.RankingRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
import com.alphasolutions.eventapi.websocket.notification.NotificationResponseMessage;
import com.alphasolutions.eventapi.websocket.notification.Status;
import org.springframework.stereotype.Service;



@Service
public class ConnectionServiceImpl implements ConnectionService {

    private final RankingRepository rankingRepository;
    public ConexaoRepository conexaoRepository;
    public UserRepository userRepository;

    public ConnectionServiceImpl(ConexaoRepository conexaoRepository, UserRepository userRepository, RankingRepository rankingRepository) {
        this.conexaoRepository = conexaoRepository;
        this.userRepository = userRepository;
        this.rankingRepository = rankingRepository;
    }

    @Override
    public boolean isConnected(User solicitante, User solicitado) {
        return conexaoRepository.existsBySolicitanteAndSolicitado(solicitante, solicitado)
                || conexaoRepository.existsBySolicitanteAndSolicitado(solicitado, solicitante);

    }

    @Override
    public NotificationResponseMessage connect (String idSolicitante, String idSolicitado, Status status) {
        User solicitante = userRepository.findByUniqueCode(idSolicitante);
        User solicitado = userRepository.findByUniqueCode(idSolicitado);
        if (solicitante == null || solicitado == null) {
            return new NotificationResponseMessage("Não foi encontrado nenhum usuário com esse código: " + (solicitante == null ? idSolicitante:idSolicitado));
        }
        if(isConnected(solicitante, solicitado)) {
            String solicitantSideStatus = conexaoRepository.findBySolicitanteAndSolicitado(solicitante,solicitado).getStatus();
            String solicitatedSideStatus = conexaoRepository.findBySolicitanteAndSolicitado(solicitado,solicitante).getStatus();

            String currentStatus = solicitantSideStatus == null? solicitatedSideStatus:solicitantSideStatus;
            if(currentStatus.equals(Status.ACCEPTED.getStatus())) {
                return new NotificationResponseMessage("Usuarios já estão conectados");
            }if(currentStatus.equals(Status.DECLINED.getStatus())) {
                return new NotificationResponseMessage("Usuario recusou sua solicitação");
            }if(currentStatus.equals(Status.WAITING.getStatus())) {
                return new NotificationResponseMessage("Aguardando resposta do outro usuário");
            }

        }
        conexaoRepository.save(new Conexao(solicitante, solicitado,status.getStatus()));
        rankingRepository.incrementConnection(solicitante.getId());
        rankingRepository.incrementConnection(solicitado.getId());
        return new NotificationResponseMessage("Sucesso!");
    }
}
