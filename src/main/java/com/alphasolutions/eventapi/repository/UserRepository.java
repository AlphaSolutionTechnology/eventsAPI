package com.alphasolutions.eventapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.alphasolutions.eventapi.model.User;
public interface UserRepository extends JpaRepository<User, String> {
    User findByUniqueCode(String uniqueCode);
    Optional<User> findById(String id);
    boolean existsByUniqueCode(String uniqueCode);
}
