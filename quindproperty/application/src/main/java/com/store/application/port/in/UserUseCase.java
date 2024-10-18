package com.store.application.port.in;

import java.util.UUID;

import com.store.domain.dto.UserClaims;
import com.store.domain.dto.UserRegistry;
import com.store.domain.error.PropertyError;

public interface UserUseCase { 
  UserClaims findById(UUID id) throws PropertyError;
  UserClaims getByEmailAndPassword(String email, String chipherPassword) throws PropertyError;
  UserClaims register(UserRegistry user) throws PropertyError;
}
