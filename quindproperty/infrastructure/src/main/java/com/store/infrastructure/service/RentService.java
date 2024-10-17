package com.store.infrastructure.service;

import java.util.UUID;

import com.store.domain.dto.CleanRent;
import com.store.domain.dto.UserClaims;
import com.store.domain.error.PropertyError;

public interface RentService {
  CleanRent rentProperty(UserClaims user, UUID property) throws PropertyError;
}
