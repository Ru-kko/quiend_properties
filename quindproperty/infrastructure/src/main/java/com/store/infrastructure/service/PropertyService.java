package com.store.infrastructure.service;

import com.store.domain.table.Property;
import com.store.domain.dto.PropertyRegistry;
import com.store.domain.error.PropertyError;

import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.UUID;

public interface PropertyService {
    Page<Property> find(BigDecimal lower, BigDecimal upper, Integer page);

    Property save(PropertyRegistry registry) throws PropertyError;

    Property update(UUID id, PropertyRegistry newData) throws PropertyError;

    Property toggleAvailability(UUID id) throws PropertyError;

    void delete(UUID id) throws PropertyError;
}
