package com.alphasolutions.eventapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUniqueCode(String uniqueCode);

    Optional<User> findById(String id);

    boolean existsByUniqueCode(String uniqueCode);

    boolean existsByEmail(String email);

    boolean existsByPalestra (Optional<Palestra> palestra);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.palestra = NULL WHERE u.palestra.id = :palestraId")
    void unsubscribeUsersFromPalestra(@Param("palestraId") Long palestraId);


    List<User> findByPalestra(Palestra palestra);

    List<User> findAllByPalestra(Palestra palestra);
}
