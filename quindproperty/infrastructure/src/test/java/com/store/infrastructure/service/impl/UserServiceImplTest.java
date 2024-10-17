package com.store.infrastructure.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import com.store.dto.UserClaims;
import com.store.dto.UserRegistry;
import com.store.error.NotFoundError;
import com.store.error.NullDataError;
import com.store.error.PropertyError;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = { "/dataIngest.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = { "/dataDrop.sql" }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserServiceImplTest {
  @Autowired
  private UserServiceImpl userService;
  @Autowired
  private JdbcTemplate jdbcTemplate;

  // ____________________________ Validate ____________________________________
  @Test
  void testThrowPropertyErrorForInvalidEmail() {
    UserRegistry userRegistry = new UserRegistry();

    userRegistry.setEmail("invalid-email");
    userRegistry.setFirstName("ValidName");
    userRegistry.setAge(25);

    assertThrows(PropertyError.class, () -> userService.validate(userRegistry));
  }

  @Test
  void testThrowNullDataErrorForNullFirstName() {
    UserRegistry userRegistry = new UserRegistry();

    userRegistry.setEmail("validemail@example.com");
    userRegistry.setFirstName(null);
    userRegistry.setAge(25);

    assertThrows(NullDataError.class, () -> userService.validate(userRegistry));
  }

  @Test
  void testThrowPropertyErrorForNullAge() {
    UserRegistry userRegistry = new UserRegistry();

    userRegistry.setEmail("validemail@example.com");
    userRegistry.setFirstName("ValidName");
    userRegistry.setAge(null); // Age is null

    assertThrows(PropertyError.class, () -> {
      userService.validate(userRegistry);
    });
  }

  @Test
  void testThrowPropertyErrorForInvalidAge() {
    UserRegistry userRegistry = new UserRegistry();

    userRegistry.setEmail("validemail@example.com");
    userRegistry.setFirstName("ValidName");
    userRegistry.setAge(0); // Invalid age (< 1)

    assertThrows(PropertyError.class, () -> userService.validate(userRegistry));
  }

  @Test
  void testPassValidationWithValidData() {
    UserRegistry userRegistry = new UserRegistry();
    
    userRegistry.setEmail("validemail@example.com");
    userRegistry.setFirstName("ValidName");
    userRegistry.setAge(25); // Valid data

    assertDoesNotThrow(() -> userService.validate(userRegistry));
  }


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

    PropertyError error = assertThrows(PropertyError.class, () -> userService.getByEmailAndPassword(email, wrongPassword));

    assertEquals(401, error.getCode());
    assertEquals("Unauthorized", error.getStatus());
  }

  @Test
  void testGetByEmailAndPasswordValidAdminCredentials() throws PropertyError {
    String email = "admin@example.com";
    String password = "adminpassword";

    UserClaims userClaims = userService.getByEmailAndPassword(email, password);

    assertNotNull(userClaims);
    assertEquals(email, userClaims.getEmail());
  }

  // ___________________________ Register __________________________________
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
