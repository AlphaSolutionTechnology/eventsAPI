package com.alphasolutions.eventapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.alphasolutions.eventapi.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUniqueCode(String uniqueCode);

    boolean existsByUniqueCode(String uniqueCode);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
