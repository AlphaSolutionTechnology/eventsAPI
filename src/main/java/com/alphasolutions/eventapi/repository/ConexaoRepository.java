package com.alphasolutions.eventapi.repository;

import com.alphasolutions.eventapi.model.entity.Conexao;
import com.alphasolutions.eventapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConexaoRepository extends JpaRepository<Conexao, Integer> {
    boolean existsBySolicitanteAndSolicitado(User solicitante, User solicitado);

    Conexao findBySolicitanteAndSolicitado(User solicitante,User solicitado);

    List<Conexao> findAllBySolicitadoAndStatus(User solicitado, String status);

    @Query(value = "SELECT CASE " +
            "WHEN id_solicitante = :userId THEN id_solicitado " +
            "ELSE id_solicitante END " +
            "FROM conexao " +
            "WHERE (id_solicitante = :userId OR id_solicitado = :userId) " +
            "AND status='ACCEPTED'",
            nativeQuery = true)
    List<String> findIdsUsuariosConectados(@Param("userId") String userId);
}
