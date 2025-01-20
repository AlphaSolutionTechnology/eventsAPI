package com.alphasolutions.eventapi.repository;

import com.alphasolutions.eventapi.model.Conexao;
import com.alphasolutions.eventapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConexaoRepository extends JpaRepository<Conexao, Integer> {
    boolean existsBySolicitanteAndSolicitado(User solicitante, User solicitado);
}
