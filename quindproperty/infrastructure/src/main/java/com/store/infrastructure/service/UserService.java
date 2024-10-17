package com.store.infrastructure.service;

import com.store.dto.UserClaims;
import com.store.dto.UserRegistry;
import com.store.error.PropertyError;

public interface UserService {
  UserClaims getByEmailAndPassword(String email, String chipherPassword) throws PropertyError;
  UserClaims register(UserRegistry user) throws PropertyError;
}
