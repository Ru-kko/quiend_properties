package com.store.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.store.application.ApplicationConfig;
import com.store.domain.Role;
import com.store.domain.dto.UserClaims;
import com.store.domain.security.TokenResponse;

import java.util.UUID;
import java.util.Date;

class JWTServiceTest {
  @Mock
  private ApplicationConfig applicationConfig;
  @InjectMocks
  private JWTService jwtService;

  private UserClaims userClaims;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(applicationConfig.getExpiration()).thenReturn(100000L);
    when(applicationConfig.getJwtSecret()).thenReturn("7hisSup3S3cre79A55WordUn#akea81e//3");

    userClaims = new UserClaims();
    userClaims.setUserId(UUID.randomUUID());
    userClaims.setLastName("Doe");
    userClaims.setEmail("test@example.com");
    userClaims.setRole(Role.USER);
  }

  @Test
  void testBuildAndVerifyToken() {
    TokenResponse tokenResponse = jwtService.buildToken(userClaims);

    assertNotNull(tokenResponse);
    assertNotNull(tokenResponse.getToken());
    assertEquals(userClaims.getUserId(), tokenResponse.getUserId());
    assertEquals(userClaims.getEmail(), tokenResponse.getEmail());
    assertTrue(tokenResponse.getExpires().after(new Date()));

    UserClaims verifiedClaims = jwtService.verifyToken(tokenResponse.getToken());

    assertNotNull(verifiedClaims);
    assertEquals(userClaims.getUserId(), verifiedClaims.getUserId());
    assertEquals(userClaims.getEmail(), verifiedClaims.getEmail());
    assertEquals(userClaims.getLastName(), verifiedClaims.getLastName());
    assertEquals(userClaims.getRole(), verifiedClaims.getRole());
  }

}
