package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.Conexao;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.repository.ConexaoRepository;
import com.alphasolutions.eventapi.repository.RankingRepository;
import com.alphasolutions.eventapi.repository.UserRepository;
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
    public boolean isConnected(String idSolicitante, String idSolicitado) {
        User solicitante = userRepository.findById(idSolicitante).orElse(null);
        User solicitado = userRepository.findById(idSolicitado).orElse(null);
        return  conexaoRepository.existsBySolicitanteAndSolicitado(solicitante,solicitado) ||
                conexaoRepository.existsBySolicitanteAndSolicitado(solicitado,solicitante);
    }

    @Override
    public void connect(String idSolicitante, String idSolicitado) {
        User solicitante = userRepository.findById(idSolicitante).orElse(null);
        User solicitado = userRepository.findById(idSolicitado).orElse(null);
        if (solicitante == null || solicitado == null) {
            throw new NullPointerException("Não encontrado nenhum usuario com o id " + (solicitante == null ? idSolicitante:idSolicitado));
        }
        if(isConnected(idSolicitante, idSolicitado)) {
            throw new IllegalArgumentException();
        }
        conexaoRepository.save(new Conexao(solicitante, solicitado));
        rankingRepository.incrementConnection(idSolicitante);
        rankingRepository.incrementConnection(idSolicitado);
    }
}
