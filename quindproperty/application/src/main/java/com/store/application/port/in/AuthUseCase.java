package com.store.application.port.in;

import com.store.domain.dto.UserClaims;
import com.store.domain.dto.UserRegistry;
import com.store.domain.error.PropertyError;
import com.store.domain.security.LoginRequest;
import com.store.domain.security.TokenResponse;

public interface AuthUseCase {
  TokenResponse register(UserRegistry user) throws PropertyError;
  TokenResponse singIn(LoginRequest credentials) throws PropertyError;
  UserClaims getClaims(String token) throws PropertyError;
}
