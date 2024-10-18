package com.store.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.store.domain.dto.PropertyRegistry;
import com.store.domain.error.PropertyError;
import com.store.domain.table.Property;

public interface PropertyUseCase {
  Page<Property> find(BigDecimal lower, BigDecimal upper, Integer page);

  Property save(PropertyRegistry registry) throws PropertyError;

  Property update(UUID id, PropertyRegistry newData) throws PropertyError;

  Property toggleAvailability(UUID id) throws PropertyError;

  void delete(UUID id) throws PropertyError;
}
