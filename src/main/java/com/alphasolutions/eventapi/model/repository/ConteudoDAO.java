package com.alphasolutions.eventapi.model.repository;

import com.alphasolutions.eventapi.model.entities.Conteudo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConteudoDAO extends JpaRepository<Conteudo, Long> {

}
