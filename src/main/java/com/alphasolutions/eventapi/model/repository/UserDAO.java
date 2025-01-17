package com.alphasolutions.eventapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.alphasolutions.eventapi.model.entities.User;
public interface UserDAO extends JpaRepository<User, Long> {
}
