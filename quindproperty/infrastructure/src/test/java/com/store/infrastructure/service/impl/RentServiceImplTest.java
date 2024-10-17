package com.store.infrastructure.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import com.store.domain.Role;
import com.store.domain.dto.CleanRent;
import com.store.domain.dto.UserClaims;
import com.store.domain.error.NotFoundError;
import com.store.domain.table.City;
import com.store.domain.table.Property;
import com.store.domain.table.Rent;
import com.store.domain.table.User;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.Date;
import java.util.Map;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = { "/dataIngest.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = { "/dataDrop.sql" }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class RentServiceImplTest {
  @Autowired
  private RentServiceImpl rentServiceImpl;
  @Autowired
  private JdbcTemplate jdbcTemplate;

  // ________________________ Parse __________________________________
  @Test
  void testParseRent() {
    User user = new User();
    user.setUserId(UUID.randomUUID());
    user.setEmail("testuser@example.com");
    user.setLastName("Doe");
    user.setRole(Role.USER);

    Property property = new Property();
    property.setPropertyId(UUID.randomUUID());
    property.setPrice(new BigDecimal("1500.00"));
    property.setImg("property_image.jpg");
    City city = new City();
    property.setLocation(city);

    Rent rent = new Rent();
    rent.setRentId(UUID.randomUUID());
    rent.setProperty(property);
    rent.setUser(user);
    rent.setRentDate(new Date());

    CleanRent result = rentServiceImpl.parseRent(rent);

    assertNotNull(result);
    assertEquals(rent.getRentId(), result.getRentID());
    assertEquals(rent.getRentDate(), result.getRentDate());

    assertNotNull(result.getProperty());
    assertEquals(property.getPrice(), result.getProperty().getPrice());
    assertEquals(property.getImg(), result.getProperty().getImage());
    assertEquals(property.getLocation(), result.getProperty().getLocation());
    assertEquals(property.getPropertyId(), result.getProperty().getPropertyId());

    assertNotNull(result.getUser());
    assertEquals(user.getEmail(), result.getUser().getEmail());
    assertEquals(user.getLastName(), result.getUser().getLastName());
    assertEquals(user.getRole(), result.getUser().getRole());
    assertEquals(user.getUserId(), result.getUser().getUserId());
  }

  @Test
  void testRentPropertySuccessful() {
    UUID userId = UUID.fromString("c9e7f827-d9ce-4b31-b4ec-33d6d9e6c789");
    UUID propertyId = UUID.fromString("48a234c4-ef02-4f96-8a04-82307b1d31a4");
    UserClaims userClaims = new UserClaims();
    userClaims.setUserId(userId);

    CleanRent cleanRent = assertDoesNotThrow(() -> rentServiceImpl.rentProperty(userClaims, propertyId));

    assertNotNull(cleanRent);
    assertEquals(userId, cleanRent.getUser().getUserId());
    assertEquals(propertyId, cleanRent.getProperty().getPropertyId());

    Boolean isAvailable = jdbcTemplate.queryForObject(
        "SELECT available FROM Property WHERE propertyId = ?",
        Boolean.class,
        propertyId);
    assertEquals(false, isAvailable);

    // Assert Rent creation in DB
    Map<String, Object> rentFromDb = jdbcTemplate.queryForMap(
        "SELECT * FROM Rent WHERE rentId = ?",
        cleanRent.getRentID());
    assertEquals(userId.toString(), rentFromDb.get("userId").toString());
    assertEquals(propertyId.toString(), rentFromDb.get("propertyId").toString());
  }

  @Test
  void testRentPropertyUserNotFound() {
    UUID invalidUserId = UUID.randomUUID(); // non-existing user
    UUID propertyId = UUID.fromString("48a234c4-ef02-4f96-8a04-82307b1d31a4"); // available property
    UserClaims userClaims = new UserClaims();
    userClaims.setUserId(invalidUserId);

    NotFoundError exception = assertThrows(NotFoundError.class,
        () -> rentServiceImpl.rentProperty(userClaims, propertyId));
    assertEquals("Not found an user with id " + invalidUserId, exception.getMessage());
  }

  @Test
  void testRentPropertyPropertyNotFoundOrUnavailable() {
    UUID userId = UUID.fromString("c9e7f827-d9ce-4b31-b4ec-33d6d9e6c789"); // valid user
    UUID unavailablePropertyId = UUID.fromString("3a9f2dab-ae3a-437f-aeaa-f0b88a60a3ee"); // Penthhouse Bogota
    UserClaims userClaims = new UserClaims();
    userClaims.setUserId(userId);

    NotFoundError exception = assertThrows(NotFoundError.class, () -> {
      rentServiceImpl.rentProperty(userClaims, unavailablePropertyId);
    });
    assertEquals("Not found a property with id " + userId, exception.getMessage());
  }
}
