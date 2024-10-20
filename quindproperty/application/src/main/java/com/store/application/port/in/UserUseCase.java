package com.store.application.port.in;

import java.util.UUID;

import com.store.domain.dto.UserClaims;
import com.store.domain.dto.UserRegistry;
import com.store.domain.error.NotFoundError;
import com.store.domain.error.PropertyError;
import com.store.domain.table.User;

public interface UserUseCase { 
  UserClaims findById(UUID id) throws PropertyError;
  User findByEmail(String email) throws NotFoundError;
  UserClaims getByEmailAndPassword(String email, String plainPassword) throws PropertyError;
  UserClaims register(UserRegistry user) throws PropertyError;
}
