package com.alphasolutions.eventapi.model.repository;

import com.alphasolutions.eventapi.model.entities.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDAO  extends JpaRepository<Role, Long> {
}
