package com.store.application.port.in;

import com.store.domain.dto.UserClaims;
import com.store.domain.security.TokenResponse;

public interface JWTUseCase {
    TokenResponse buildToken(UserClaims userClaims);
    UserClaims verifyToken(String token);
}
