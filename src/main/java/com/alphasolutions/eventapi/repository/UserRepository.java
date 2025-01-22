package com.alphasolutions.eventapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.alphasolutions.eventapi.model.User;
public interface UserRepository extends JpaRepository<User, String> {
    User findByUniqueCode(String uniqueCode);

}
