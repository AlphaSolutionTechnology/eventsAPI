package com.alphasolutions.eventapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alphasolutions.eventapi.model.User;

public interface UserProfileUpdateRepository extends JpaRepository<User, Long> {
    

} 