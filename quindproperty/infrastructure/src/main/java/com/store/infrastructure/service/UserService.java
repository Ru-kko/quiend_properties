package com.store.infrastructure.service;

import com.store.domain.dto.UserClaims;
import com.store.domain.dto.UserRegistry;
import com.store.domain.error.PropertyError;

public interface UserService {
  UserClaims getByEmailAndPassword(String email, String chipherPassword) throws PropertyError;
  UserClaims register(UserRegistry user) throws PropertyError;
}
