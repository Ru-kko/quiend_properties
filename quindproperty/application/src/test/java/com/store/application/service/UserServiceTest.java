package com.store.application.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.store.application.port.out.PasswordEncoder;
import com.store.application.port.out.UserRepository;
import com.store.domain.Role;
import com.store.domain.dto.UserClaims;
import com.store.domain.dto.UserRegistry;
import com.store.domain.error.NotFoundError;
import com.store.domain.error.NullDataError;
import com.store.domain.error.PropertyError;
import com.store.domain.table.User;

class UserServiceTest {
  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

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

    User mockUser = new User(
        UUID.randomUUID(),
        email,
        "John",
        "Doe",
        30,
        password,
        Role.USER);

    when(passwordEncoder.matches(password, password)).thenReturn(true);
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

    UserClaims userClaims = userService.getByEmailAndPassword(email, password);

    assertNotNull(userClaims);
    assertEquals(email, userClaims.getEmail());
  }

  @Test
  void testGetByEmailAndPasswordUserNotFound() {
    String email = "nonexistent@example.com";
    String password = "somepassword";

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
    assertThrows(NotFoundError.class, () -> userService.getByEmailAndPassword(email, password));
  }

  @Test
  void testGetByEmailAndPasswordIncorrectPassword() {
    String email = "user1@example.com";
    String wrongPassword = "wrongpassword";

    User mockUser = new User(
        UUID.randomUUID(),
        email,
        "John",
        "Doe",
        30,
        "pass",
        Role.USER);

    when(passwordEncoder.matches(wrongPassword, mockUser.getPassword())).thenReturn(false);
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

    PropertyError error = assertThrows(PropertyError.class,
        () -> userService.getByEmailAndPassword(email, wrongPassword));

    assertEquals(401, error.getCode());
    assertEquals("Unauthorized", error.getStatus());
  }

  // __________________________ Register _________________________________
  @Test
  void testRegisterNewUserSuccessfully() throws PropertyError {
    var newUser = new UserRegistry(
        "newuser@example.com", "newpassword", "New", "User", 20);

    User mockUser = new User();
    mockUser.setEmail(newUser.getEmail());
    mockUser.setFirstName(newUser.getFirstName());
    mockUser.setLastName(newUser.getLastName());
    mockUser.setAge(newUser.getAge());

    when(userRepository.save(any(User.class))).thenReturn(mockUser);
    userService.register(newUser);

    verify(userRepository, times(1)).save(any(User.class));
  }
}
