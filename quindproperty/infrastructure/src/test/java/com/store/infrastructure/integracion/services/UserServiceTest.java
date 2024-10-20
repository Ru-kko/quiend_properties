package com.store.infrastructure.integracion.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.store.application.port.in.UserUseCase;
import com.store.domain.dto.UserClaims;
import com.store.domain.dto.UserRegistry;
import com.store.domain.error.NotFoundError;
import com.store.domain.error.PropertyError;
import com.store.infrastructure.TestConfiguration;

@SpringBootTest(classes = { TestConfiguration.class })
@TestInstance(Lifecycle.PER_CLASS)
@Sql(scripts = { "/dataIngest.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = { "/dataDrop.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class UserServiceTest {
  @Autowired
  private UserUseCase userService;
  @Autowired
  private JdbcTemplate jdbcTemplate;

  // ____________________________ GetByEmailAndPassword _______________________

  @Test
  void testGetByEmailAndPasswordValidCredentials() throws PropertyError {
    String email = "user1@example.com";
    String password = "userpassword1";

    UserClaims userClaims = userService.getByEmailAndPassword(email, password);

    assertNotNull(userClaims);
    assertEquals(email, userClaims.getEmail());
  }

  @Test
  void testGetByEmailAndPasswordUserNotFound() {
    String email = "nonexistent@example.com";
    String password = "somepassword";

    assertThrows(NotFoundError.class, () -> userService.getByEmailAndPassword(email, password));
  }

  @Test
  void testGetByEmailAndPasswordIncorrectPassword() {
    String email = "user1@example.com";
    String wrongPassword = "wrongpassword";

    PropertyError error = assertThrows(PropertyError.class,
        () -> userService.getByEmailAndPassword(email, wrongPassword));

    assertEquals(401, error.getCode());
    assertEquals("Unauthorized", error.getStatus());
  }

  @Test
  void testRegisterNewUserSuccessfully() throws PropertyError {
    var newUser = new UserRegistry(
      "newuser@example.com", "newpassword", "New", "User", 20
    );

    var result = userService.register(newUser);
    var fromDb = jdbcTemplate.queryForMap("select * from \"User\" where userId = ?", result.getUserId());

    assertNotNull(result);
    assertEquals(newUser.getEmail(), fromDb.get("email"));
    assertEquals(newUser.getLastName(), fromDb.get("lastName"));
    assertEquals(newUser.getFirstName(), fromDb.get("firstName"));
    assertEquals(newUser.getPassword(), fromDb.get("password"));
  }

  @Test
  void register_ShouldThrowPropertyError_WhenDuplicateEmail() {
    UserRegistry duplicateUser = new UserRegistry(
      "user1@example.com", "newpassword", "New", "User", 22
    );

    PropertyError exception = assertThrows(PropertyError.class, () -> userService.register(duplicateUser));

    assertEquals(400, exception.getCode());
  }
}
