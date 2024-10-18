package com.store.infrastructure.persistnce.adapter;

import java.util.Optional;
import java.util.UUID;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.store.application.port.out.UserRepository;
import com.store.domain.error.PropertyError;
import com.store.domain.table.User;
import com.store.infrastructure.persistnce.repository.JPAUserRepository;

import lombok.AllArgsConstructor;


@Repository
@AllArgsConstructor
public class JPAUserRepositoryAdapter implements UserRepository {
  private JPAUserRepository userRepository;

  @Override
  public User save(User user) throws PropertyError {
    try {
      return userRepository.save(user);
    } catch (DataIntegrityViolationException e) {
      isDuplicateNameViolation(e);
    }
    return null;
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }
  
  @Override
  public Optional<User> findById(UUID id) {
    return userRepository.findById(id);
  }
  
  private void isDuplicateNameViolation(DataIntegrityViolationException e) throws PropertyError {
    Throwable rootCause = e.getCause();
    if (!(rootCause instanceof ConstraintViolationException)) {
      throw e;
    }
    String sqlState = ((org.hibernate.exception.ConstraintViolationException) rootCause).getSQLState();
    if ("23505".equals(sqlState)) {
      throw PropertyError.badRequest("Already exists an user with this email", e);
    }
    throw e;
  }
}