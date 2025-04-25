package com.alphasolutions.eventapi.repository;

import java.util.List;
import java.util.Optional;

import com.alphasolutions.eventapi.model.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import com.alphasolutions.eventapi.model.entity.Palestra;
import com.alphasolutions.eventapi.model.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUniqueCode(String uniqueCode);

    Optional<User> findByIdUser(String idUser);

    boolean existsByUniqueCode(String uniqueCode);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.palestraAtual = NULL WHERE u.palestraAtual.idPalestra = :palestraId")
    void unsubscribeUsersFromPalestraAtual(@Param("palestraId") Long palestraId);


    List<User> findByPalestraAtual(Palestra palestraAtual);

    List<User> findAllByPalestraAtual(Palestra palestraAtual);

    List<User> findAllByEvento(Evento evento);
}
