package com.alphasolutions.eventapi.repository;

import com.alphasolutions.eventapi.model.Conexao;
import com.alphasolutions.eventapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConexaoRepository extends JpaRepository<Conexao, Integer> {
    boolean existsBySolicitanteAndSolicitado(User solicitante, User solicitado);

    Conexao findBySolicitanteAndSolicitado(User solicitante,User solicitado);

    List<Conexao> findAllBySolicitadoAndStatus(User solicitado, String status);

}
