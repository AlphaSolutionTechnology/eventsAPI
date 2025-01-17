package com.alphasolutions.eventapi.model.repository;

import com.alphasolutions.eventapi.model.entities.Palestra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PalestraDAO extends JpaRepository<Palestra, Long> {

}
