package com.store.application.port.out;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.store.domain.error.PropertyError;
import com.store.domain.table.Property;

public interface PropertyRepository {
  Page<Property> findAllWithLowerPrice(BigDecimal lowerPrice, Pageable pageable);

  Page<Property> findAllWithPriceRange(BigDecimal lowerPrice, BigDecimal upperPrice, Pageable pageable);

  Property save(Property property) throws PropertyError;

  Optional<Property> findById(UUID id);
}