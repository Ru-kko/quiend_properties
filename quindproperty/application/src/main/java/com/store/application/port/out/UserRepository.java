package com.store.application.port.out;

import com.store.domain.error.PropertyError;
import com.store.domain.table.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
  Optional<User> findById(UUID id);
  User save(User user) throws PropertyError;
  Optional<User> findByEmail(String email);  
}
