package com.store.infrastructure.service.impl;

import com.store.domain.table.Property;
import com.store.infrastructure.config.InfraProperties;
import com.store.infrastructure.persistence.PropertyRepository;
import com.store.infrastructure.service.PropertyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
class PropertyServiceImpl implements PropertyService {
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private InfraProperties props;

    @Override
    public Page<Property> find(BigDecimal lower, BigDecimal upper, Integer page) {
        Pageable pageable = PageRequest.of(page, props.getPageSize());

        BigDecimal finalLower = lower == null ? BigDecimal.ZERO : lower;

        if (upper == null) {
            return propertyRepository.findAllWithLowerPrice(finalLower, pageable);
        }

        return propertyRepository.findAllWithPriceRange(lower, upper, pageable);
    }
}
