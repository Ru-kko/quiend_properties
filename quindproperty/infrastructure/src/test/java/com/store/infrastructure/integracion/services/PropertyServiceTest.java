package com.store.infrastructure.integracion.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.store.application.port.in.PropertyUseCase;
import com.store.domain.dto.PropertyRegistry;
import com.store.domain.error.PropertyError;
import com.store.domain.table.Property;
import com.store.infrastructure.TestConfiguration;

@SpringBootTest(classes = { TestConfiguration.class })
@TestInstance(Lifecycle.PER_CLASS)
@Sql(scripts = { "/dataIngest.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = { "/dataDrop.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class PropertyServiceTest {
  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  private PropertyUseCase propertyService;

  // Finding
  @Test
  void findWithoutBound() {
    var page = 0;
    var res = propertyService.find(null, null, page);

    var fromDb = jdbcTemplate.queryForObject("select count(*) from Property WHERE active = true AND available = true",
        Long.class);

    assertEquals(fromDb, res.getTotalElements());
  }

  @Test
  void findWithLeftBound() {
    var page = 0;
    var res = propertyService.find(new BigDecimal(700000), null, page);

    var fromDb = jdbcTemplate.queryForObject(
        "select count(*) from Property WHERE active = true AND available = true AND price >= 700000",
        Long.class);
    assertEquals(res.getTotalElements(), fromDb);
  }

  @Test
  void findWithBothBounds() {
    var page = 0;
    var res = propertyService.find(new BigDecimal(500000), new BigDecimal(800000), page);

    var fromDb = jdbcTemplate.queryForObject("select count(*) from Property WHERE active = true AND available = true " +
        "AND (price >= 500000 AND price <= 800000)",
        Long.class);
    assertEquals(res.getTotalElements(), fromDb);
  }

  // Save
  @Test
  void testSaveRepeatedName() {
    PropertyRegistry repeated = new PropertyRegistry("Luxury Apartment Medellin",
        UUID.fromString("e9c1a570-dbe4-4a2e-a71e-2fd5a7b7f123"), "image.png", new BigDecimal("100000"));

    assertThrows(PropertyError.class, () -> propertyService.save(repeated));
  }

  // Update
  @Test
  void testUpdatePropertyRentedChangePrice() {
    UUID rentedPropertyId = UUID.fromString("3a9f2dab-ae3a-437f-aeaa-f0b88a60a3ee"); // Penthouse Bogota
    PropertyRegistry newData = new PropertyRegistry();
    newData.setPrice(new BigDecimal(1100000));

    PropertyError exception = assertThrows(PropertyError.class,
        () -> propertyService.update(rentedPropertyId, newData));
    assertEquals(400, exception.getCode());
  }

  @Test
  void testUpdatePropertyRentedChangeLocation() {
    UUID rentedPropertyId = UUID.fromString("3a9f2dab-ae3a-437f-aeaa-f0b88a60a3ee"); // Penthouse Bogota
    PropertyRegistry newData = new PropertyRegistry();
    newData.setLocation(UUID.fromString("c21d6f5e-7b58-4d81-9fc8-91e7c69d6e9a"));

    PropertyError exception = assertThrows(PropertyError.class,
        () -> propertyService.update(rentedPropertyId, newData));
    assertEquals(400, exception.getCode());
  }

  @Test
  void testUpdateAvailablePropertyFullData() {
    UUID availablePropertyId = UUID.fromString("48a234c4-ef02-4f96-8a04-82307b1d31a4"); // Luxury Apartment Medellin
    PropertyRegistry newData = new PropertyRegistry();
    newData.setName("Updated Apartment Medellin");
    newData.setPrice(new BigDecimal("3500000.00"));
    newData.setLocation(UUID.fromString("c21d6f5e-7b58-4d81-9fc8-91e7c69d6e9a")); // Cali
    newData.setImage("newimage.jpg");

    Property updatedProperty = assertDoesNotThrow(() -> propertyService.update(availablePropertyId, newData));
    assertNotNull(updatedProperty);
    assertEquals(availablePropertyId, updatedProperty.getPropertyId());

    var fromDb = jdbcTemplate.queryForMap("SELECT * FROM Property WHERE propertyId = ?",
        updatedProperty.getPropertyId());

    assertEquals(newData.getName(), fromDb.get("name"));
    assertEquals(newData.getPrice(), fromDb.get("price"));
    assertEquals(newData.getLocation().toString(), fromDb.get("cityId").toString());
    assertEquals(newData.getImage(), fromDb.get("img"));
  }

  // Toggle availability
  @Test
  void testToggleAvailabilityPropertyInactive() {
    UUID inactivePropertyId = UUID.fromString("c8447bdf-559f-465b-9333-2c2dc38addbf");

    jdbcTemplate.update("UPDATE Property SET active = FALSE WHERE propertyId = ?", inactivePropertyId);

    PropertyError exception = assertThrows(PropertyError.class,
        () -> propertyService.toggleAvailability(inactivePropertyId));

    assertEquals(404, exception.getCode());
  }

  // Delete
  @Test
  void testDeleteValidProperty() {
    PropertyRegistry newProperty = new PropertyRegistry("New Apartment",
        UUID.fromString("a4b2c9d7-258e-4f2f-a1ad-1c7f5f2a9d75"), "image.jpg", new BigDecimal("2500000.00"));

    Property savedProperty = assertDoesNotThrow(() -> propertyService.save(newProperty));
    UUID newPropertyId = savedProperty.getPropertyId();

    assertDoesNotThrow(() -> propertyService.delete(newPropertyId));

    var fromDb = jdbcTemplate.queryForObject("SELECT active FROM Property WHERE propertyId = ?", Boolean.class,
        newPropertyId);
    assertEquals(false, fromDb);
  }
}
