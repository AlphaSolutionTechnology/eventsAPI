package com.alphasolutions.eventapi.websocket.service;

import com.alphasolutions.eventapi.exception.AlreadyConnectedUsersException;
import com.alphasolutions.eventapi.exception.SelfConnectionException;
import com.alphasolutions.eventapi.exception.UserNotFoundException;
import com.alphasolutions.eventapi.exception.WaitingForResponseException;
import com.alphasolutions.eventapi.model.entity.Conexao;
import com.alphasolutions.eventapi.model.entity.User;
import com.alphasolutions.eventapi.model.dto.UserConnetionDTO;
import com.alphasolutions.eventapi.repository.ConexaoRepository;
import com.alphasolutions.eventapi.repository.RankingRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
import com.alphasolutions.eventapi.utils.JwtUtil;
import com.alphasolutions.eventapi.websocket.notification.Status;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class ConnectionServiceImpl implements ConnectionService {

    private final RankingRepository rankingRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtUtil jwtUtil;
    public ConexaoRepository conexaoRepository;
    public UserRepository userRepository;

    public ConnectionServiceImpl(ConexaoRepository conexaoRepository, UserRepository userRepository, RankingRepository rankingRepository, SimpMessagingTemplate messagingTemplate, JwtUtil jwtUtil) {
        this.conexaoRepository = conexaoRepository;
        this.userRepository = userRepository;
        this.rankingRepository = rankingRepository;
        this.messagingTemplate = messagingTemplate;

        this.jwtUtil = jwtUtil;
    }
    @Override
    public boolean isConnected(User solicitante, User solicitado) {
        return conexaoRepository.existsBySolicitanteAndSolicitado(solicitante, solicitado)
                || conexaoRepository.existsBySolicitanteAndSolicitado(solicitado, solicitante);

    }
    @Override
    public void connect(String idSolicitante, String idSolicitado, Status status) {

        if (idSolicitante.equals(idSolicitado)) {
            throw new SelfConnectionException("You cannot connect with yourself");
        }

        User solicitante = userRepository.findByUniqueCode(idSolicitante);
        User solicitado = userRepository.findByUniqueCode(idSolicitado);

        if (solicitante == null || solicitado == null) {
            throw new UserNotFoundException("No user found with ID: " + (solicitante == null ? idSolicitante : idSolicitado));
        }

        if (isConnected(solicitante, solicitado)) {
            Conexao solicitantSideStatus = conexaoRepository.findBySolicitanteAndSolicitado(solicitante, solicitado);
            Conexao solicitatedSideStatus = conexaoRepository.findBySolicitanteAndSolicitado(solicitado, solicitante);

            Conexao currentStatus = solicitantSideStatus == null ? solicitatedSideStatus : solicitantSideStatus;

            if (currentStatus.getStatus().equals(Status.ACCEPTED.getStatus())) {
                throw new AlreadyConnectedUsersException("Users are already connected");
            }
            if (currentStatus.getStatus().equals(Status.WAITING.getStatus())) {
                String message = idSolicitante.equals(currentStatus.getSolicitado().getUniqueCode()) 
                    ? "This user has sent you a connection request. Accept if you want to connect with them."
                    : "Waiting for the other user's response";
                throw new WaitingForResponseException(message);
            }
        }

        conexaoRepository.save(new Conexao(solicitante, solicitado, status.getStatus()));
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
            rankingRepository.incrementConnection(solicitante.getIdUser());
            rankingRepository.incrementConnection(solicitado.getIdUser());
            messagingTemplate.convertAndSend("/topic/ranking", Map.of("type","ranking_update"));
        }else{
            conexao.setStatus(Status.DECLINED.getStatus());
            conexaoRepository.delete(conexao);
        }

    }

    @Override
    public List<UserConnetionDTO> getAcceotedConnections(String token) {
        Map<String,Object> verifiedToken =  jwtUtil.extractClaim(token);
        String id = verifiedToken.get("id").toString();
        List<String> userIdList = conexaoRepository.findIdsUsuariosConectados(id);
        List<UserConnetionDTO> userConnetionDTOS = new ArrayList<>(userIdList.size());
        User user;
        for(String u : userIdList) {
            user = userRepository.findById(u).orElse(null);

            assert user != null;
            userConnetionDTOS.add(new UserConnetionDTO(user.getNome(), user.getUniqueCode()));
        }
        return userConnetionDTOS;
    }

    public List<Conexao> getPendingConnections(String token) {
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
