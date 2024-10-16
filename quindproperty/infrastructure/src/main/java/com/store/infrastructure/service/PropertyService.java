package com.store.infrastructure.service;

import com.store.domain.table.Property;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface PropertyService {
    Page<Property> find(BigDecimal lower, BigDecimal upper, Integer page);
}
