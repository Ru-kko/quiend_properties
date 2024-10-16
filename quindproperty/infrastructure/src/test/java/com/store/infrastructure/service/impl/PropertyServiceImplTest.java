package com.store.infrastructure.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import com.store.infrastructure.service.PropertyService;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = { "/dataIngest.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = { "/dataDrop.sql" }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PropertyServiceImplTest {
  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  private PropertyService propertyService;

  // ___________________ PropertyService.Find() ___________________
  @Test
  void findWithoutBound() {
    var page = 0;
    var res = propertyService.find(null, null, page);

    var fromDb = jdbcTemplate.queryForObject("select count(*) from Property WHERE active = true AND available = true",
        Long.class);
    assertEquals(res.getTotalElements(), fromDb);
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
}
