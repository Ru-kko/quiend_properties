package com.store.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.store.application.port.out.PropertyRepository;
import com.store.application.port.out.RentRepository;
import com.store.application.port.out.UserRepository;
import com.store.domain.Role;
import com.store.domain.dto.CleanRent;
import com.store.domain.dto.UserClaims;
import com.store.domain.error.NotFoundError;
import com.store.domain.error.PropertyError;
import com.store.domain.table.City;
import com.store.domain.table.Property;
import com.store.domain.table.Rent;
import com.store.domain.table.User;

class RentServiceTest {
  @Mock
  private RentRepository rentRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PropertyRepository propertyRepository;

  @Mock
  private PropertyService propertyService;

  @InjectMocks
  private RentService rentService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testRentPropertySuccessful() throws PropertyError {
    UserClaims userClaims = new UserClaims();
    userClaims.setUserId(UUID.randomUUID());
    userClaims.setEmail("user@example.com");
    userClaims.setLastName("Doe");

    UUID propertyId = UUID.randomUUID();
    Property mockProperty = new Property();
    mockProperty.setPropertyId(propertyId);
    mockProperty.setAvailable(true);
    mockProperty.setActive(true);
    mockProperty.setPrice(new BigDecimal("2000.00"));
    mockProperty.setImg("property.jpg");
    mockProperty.setLocation(new City(UUID.randomUUID(), "Some"));

    Rent mockRent = new Rent();
    mockRent.setRentId(UUID.randomUUID());
    mockRent.setUser(new User(userClaims.getUserId(), userClaims.getEmail(), "jonh", null, 20, "psw", Role.USER));
    mockRent.setProperty(mockProperty);

    when(userRepository.findById(userClaims.getUserId())).thenReturn(Optional.of(new User()));
    when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(mockProperty));
    when(rentRepository.save(any(Rent.class))).thenReturn(mockRent);

    CleanRent result = rentService.rentProperty(userClaims, propertyId);

    assertNotNull(result);
    assertEquals(mockRent.getRentId(), result.getRentID());
    assertEquals(mockProperty.getPrice(), result.getProperty().getPrice());
    assertEquals(mockProperty.getImg(), result.getProperty().getImage());
    assertEquals(mockProperty.getLocation(), result.getProperty().getLocation());
    assertEquals(userClaims.getEmail(), result.getUser().getEmail());
  }

  @Test
  void testRentPropertyUserNotFound() {
    UserClaims userClaims = new UserClaims();
    userClaims.setUserId(UUID.randomUUID());
    UUID propertyId = UUID.randomUUID();

    when(userRepository.findById(userClaims.getUserId())).thenReturn(Optional.empty());

    NotFoundError exception = assertThrows(NotFoundError.class, () -> rentService.rentProperty(userClaims, propertyId));

    assertEquals("Not found an user with id " + userClaims.getUserId(), exception.getMessage());
  }

  @Test
  void testRentPropertyPropertyNotFoundOrUnavailable() {
    UserClaims userClaims = new UserClaims();
    userClaims.setUserId(UUID.randomUUID());
    UUID propertyId = UUID.randomUUID();

    when(userRepository.findById(userClaims.getUserId())).thenReturn(Optional.of(new User()));
    when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

    NotFoundError exception = assertThrows(NotFoundError.class, () -> rentService.rentProperty(userClaims, propertyId));

    assertEquals("Not found a property with id " + userClaims.getUserId(), exception.getMessage());
  }

  @Test
  void testRentPropertyPropertyUnavailable() {
    UserClaims userClaims = new UserClaims();
    userClaims.setUserId(UUID.randomUUID());
    UUID propertyId = UUID.randomUUID();

    when(userRepository.findById(userClaims.getUserId())).thenReturn(Optional.of(new User()));

    Property mockProperty = new Property();
    mockProperty.setAvailable(false); // Unavailable property
    mockProperty.setActive(true);

    when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(mockProperty));

    NotFoundError exception = assertThrows(NotFoundError.class, () -> rentService.rentProperty(userClaims, propertyId));

    assertEquals("Not found a property with id " + userClaims.getUserId(), exception.getMessage());
  }

  @Test
  void testParseRent() {
    Rent rent = new Rent();
    Property property = new Property();
    property.setPropertyId(UUID.randomUUID());
    property.setPrice(new BigDecimal("1500.00"));
    property.setImg("img.jpg");
    property.setLocation(new City());
    rent.setProperty(property);

    User user = new User();
    user.setUserId(UUID.randomUUID());
    user.setEmail("test@example.com");
    user.setLastName("Smith");
    rent.setUser(user);

    rent.setRentId(UUID.randomUUID());

    CleanRent result = rentService.parseRent(rent);

    assertNotNull(result);
    assertEquals(rent.getRentId(), result.getRentID());
    assertEquals(property.getPrice(), result.getProperty().getPrice());
    assertEquals(property.getImg(), result.getProperty().getImage());
    assertEquals(property.getLocation(), result.getProperty().getLocation());
    assertEquals(user.getEmail(), result.getUser().getEmail());
  }
}
