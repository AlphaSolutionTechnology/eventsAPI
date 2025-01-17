package com.alphasolutions.eventapi.model.repository;

import com.alphasolutions.eventapi.model.entities.Conexao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConexaoDAO extends JpaRepository<Conexao, Integer> {

}
