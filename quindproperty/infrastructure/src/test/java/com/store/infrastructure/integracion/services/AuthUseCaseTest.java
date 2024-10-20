package com.store.infrastructure.integracion.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.store.application.port.in.AuthUseCase;
import com.store.domain.dto.UserRegistry;
import com.store.domain.error.PropertyError;
import com.store.domain.security.LoginRequest;
import com.store.domain.security.TokenResponse;
import com.store.infrastructure.TestConfiguration;

@SpringBootTest(classes = { TestConfiguration.class })
@TestInstance(Lifecycle.PER_CLASS)
@Sql(scripts = { "/dataIngest.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = { "/dataDrop.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class AuthUseCaseTest {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private AuthUseCase authService;

  @Test
  void testRegisterSuccess() throws PropertyError {
    UserRegistry newUser = new UserRegistry("newuser@example.com", "newpassword", "New", "User", 20);

    TokenResponse tokenResponse = authService.register(newUser);

    assertNotNull(tokenResponse.getToken());

    var fromDb = jdbcTemplate.queryForMap("select * from \"User\" where userId = ?", tokenResponse.getUserId());

    assertEquals(newUser.getEmail(), fromDb.get("email"));
    assertEquals(newUser.getLastName(), fromDb.get("lastName"));
    assertEquals(newUser.getFirstName(), fromDb.get("firstName"));
    assertEquals(newUser.getPassword().concat("encode"), fromDb.get("password"));
  }

  @Test
  void testSignInSuccess() throws PropertyError {
    LoginRequest loginRequest = new LoginRequest("user1@example.com", "userpassword1");
    TokenResponse tokenResponse = authService.singIn(loginRequest);

    assertNotNull(tokenResponse.getToken());

    var fromDb = jdbcTemplate.queryForMap("select * from \"User\" where userId = ?", tokenResponse.getUserId());

    assertEquals(loginRequest.getEmail(), fromDb.get("email"));
    assertEquals(tokenResponse.getUserId(), fromDb.get("userId"));
    assertEquals(loginRequest.getPassword().concat("encode"), fromDb.get("password"));
  }
}
