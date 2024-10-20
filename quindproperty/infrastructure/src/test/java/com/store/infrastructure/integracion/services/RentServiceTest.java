package com.store.infrastructure.integracion.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.store.application.port.in.RentUseCase;
import com.store.domain.dto.CleanRent;
import com.store.domain.dto.UserClaims;
import com.store.infrastructure.TestConfiguration;

@SpringBootTest(classes = { TestConfiguration.class })
@TestInstance(Lifecycle.PER_CLASS)
@Sql(scripts = { "/dataIngest.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = { "/dataDrop.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class RentServiceTest {
  @Autowired
  private RentUseCase rentService;
  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  void testRentPropertySuccessful() {
    UUID userId = UUID.fromString("c9e7f827-d9ce-4b31-b4ec-33d6d9e6c789");
    UUID propertyId = UUID.fromString("48a234c4-ef02-4f96-8a04-82307b1d31a4");
    UserClaims userClaims = new UserClaims();
    userClaims.setUserId(userId);

    CleanRent cleanRent = assertDoesNotThrow(() -> rentService.rentProperty(userClaims, propertyId));

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
}
