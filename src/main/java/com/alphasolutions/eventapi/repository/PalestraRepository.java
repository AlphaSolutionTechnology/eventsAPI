package com.alphasolutions.eventapi.repository;

import com.alphasolutions.eventapi.model.Palestra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PalestraRepository extends JpaRepository<Palestra, Long> {

}
