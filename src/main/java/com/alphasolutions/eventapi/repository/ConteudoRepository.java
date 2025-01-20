package com.alphasolutions.eventapi.repository;

import com.alphasolutions.eventapi.model.Conteudo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConteudoRepository extends JpaRepository<Conteudo, Long> {

}
