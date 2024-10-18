package com.store.infrastructure.persistnce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.domain.table.User;

import java.util.Optional;
import java.util.UUID;

public interface JPAUserRepository extends JpaRepository<User, UUID> { 
  Optional<User> findByEmail(String email);
}
