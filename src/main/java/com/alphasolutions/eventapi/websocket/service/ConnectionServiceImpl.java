package com.alphasolutions.eventapi.websocket.service;

import com.alphasolutions.eventapi.model.Conexao;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.repository.ConexaoRepository;
import com.alphasolutions.eventapi.repository.RankingRepository;
import com.alphasolutions.eventapi.repository.RankingViewRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
import com.alphasolutions.eventapi.utils.JwtUtil;
import com.alphasolutions.eventapi.websocket.notification.NotificationResponseMessage;
import com.alphasolutions.eventapi.websocket.notification.Status;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class ConnectionServiceImpl implements ConnectionService {

    private final RankingRepository rankingRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RankingViewRepository rankingViewRepository;
    private final JwtUtil jwtUtil;
    public ConexaoRepository conexaoRepository;
    public UserRepository userRepository;

    public ConnectionServiceImpl(ConexaoRepository conexaoRepository, UserRepository userRepository, RankingRepository rankingRepository, SimpMessagingTemplate messagingTemplate, RankingViewRepository rankingViewRepository, JwtUtil jwtUtil) {
        this.conexaoRepository = conexaoRepository;
        this.userRepository = userRepository;
        this.rankingRepository = rankingRepository;
        this.messagingTemplate = messagingTemplate;
        this.rankingViewRepository = rankingViewRepository;
        this.jwtUtil = jwtUtil;
    }
    @Override
    public boolean isConnected(User solicitante, User solicitado) {
        return conexaoRepository.existsBySolicitanteAndSolicitado(solicitante, solicitado)
                || conexaoRepository.existsBySolicitanteAndSolicitado(solicitado, solicitante);

    }
    @Override
    public NotificationResponseMessage connect (String idSolicitante, String idSolicitado, Status status) {
        if(idSolicitante.equals(idSolicitado)) {
            return new NotificationResponseMessage("Você não pode enviar solicitação para sí!");
        }
        User solicitante = userRepository.findByUniqueCode(idSolicitante);
        User solicitado = userRepository.findByUniqueCode(idSolicitado);
        if(solicitante == null || solicitado == null) {
            return new NotificationResponseMessage("Não foi encontrado nenhum usuário com esse código: " + (solicitado == null ? idSolicitado:idSolicitante));
        }

        if(isConnected(solicitante, solicitado)) {
            Conexao solicitantSideStatus = conexaoRepository.findBySolicitanteAndSolicitado(solicitante,solicitado);
            Conexao solicitatedSideStatus = conexaoRepository.findBySolicitanteAndSolicitado(solicitado,solicitante);
            String currentStatus = solicitantSideStatus == null? solicitatedSideStatus.getStatus():solicitantSideStatus.getStatus();
            if(currentStatus.equals(Status.ACCEPTED.getStatus())) {
                return new NotificationResponseMessage("Usuarios já estão conectados");
            }if(currentStatus.equals(Status.WAITING.getStatus())) {
                return new NotificationResponseMessage("Aguardando resposta do outro usuário");
            }
        }
        conexaoRepository.save(new Conexao(solicitante, solicitado,status.getStatus()));
        return new NotificationResponseMessage("Sucesso!");
    }

    @Override
    public void answerConnectionRequest(String to, String from, String status) {
        if(to == null || to.isEmpty() || from == null || from.isEmpty()) {
            throw new NullPointerException(("User foi null: ") + (to == null?from:to));
        }
        User solicitante = userRepository.findByUniqueCode(from);
        User solicitado = userRepository.findByUniqueCode(to);
        Conexao conexao = conexaoRepository.findBySolicitanteAndSolicitado(solicitante, solicitado);
        if(status.equals(Status.ACCEPTED.getStatus())) {
            conexao.setStatus(Status.ACCEPTED.getStatus());
            conexaoRepository.save(conexao);
            rankingRepository.incrementConnection(solicitante.getId());
            rankingRepository.incrementConnection(solicitado.getId());
            messagingTemplate.convertAndSend("/topic/ranking", Map.of("type","ranking_update"));
        }else{
            conexao.setStatus(Status.DECLINED.getStatus());
            conexaoRepository.delete(conexao);
        }

    }
    @Override
    public List<Conexao> getConexoes(String token) {
        try{
            Map<String,Object> claim = jwtUtil.extractClaim(token);
            String userId = (String)claim.get("id");
            User user = userRepository.findById(userId).orElse(null);
            return conexaoRepository.findAllBySolicitadoAndStatus(user,Status.WAITING.getStatus());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
