package com.store.infrastructure.service.impl;

import com.store.domain.table.City;
import com.store.domain.table.Property;
import com.store.dto.PropertyRegistry;
import com.store.error.NullDataError;
import com.store.error.PropertyError;
import com.store.infrastructure.config.InfraProperties;
import com.store.infrastructure.persistence.CityRepository;
import com.store.infrastructure.persistence.PropertyRepository;
import com.store.infrastructure.service.PropertyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
class PropertyServiceImpl implements PropertyService {
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private InfraProperties props;
    @Autowired
    private CityRepository cityRepository;

    @Override
    public Page<Property> find(BigDecimal lower, BigDecimal upper, Integer page) {
        Pageable pageable = PageRequest.of(page, props.getPageSize());

        BigDecimal finalLower = lower == null ? BigDecimal.ZERO : lower;

        if (upper == null) {
            return propertyRepository.findAllWithLowerPrice(finalLower, pageable);
        }

        return propertyRepository.findAllWithPriceRange(lower, upper, pageable);
    }

    @Override
    public Property save(PropertyRegistry registry) throws PropertyError {
        Property base = transform(registry);

        base.setActive(true);

        try {
            base = propertyRepository.save(base);
        } catch (DataIntegrityViolationException e) {
            isDuplicateNameViolation(e);
        }

        return base;
    }
    
    /**
     * Checks if is repeated name error
     * ! this only works on postgre
     * @param e
     * @return
     */
    private void isDuplicateNameViolation(DataIntegrityViolationException e) throws PropertyError {
        Throwable rootCause = e.getRootCause();
        if (!(rootCause instanceof org.hibernate.exception.ConstraintViolationException)) {
            throw e;
        }

        String sqlState = ((org.hibernate.exception.ConstraintViolationException) rootCause).getSQLState();
        if ("23505".equals(sqlState)) {
            throw new PropertyError("Already exists a property with this name", e, 404, "BadRequest");
        }

        throw e;
    }

    Property transform(PropertyRegistry dto) throws PropertyError {
        Property res = new Property();

        if (dto.getName() == null)
            throw new NullDataError("Not provided name");
        if (dto.getImage() == null)
            throw new NullDataError("Not provided image");
        if (dto.getLocation() == null)
            throw new NullDataError("Not provided valid location id");
        if (dto.getPrice() == null)
            throw new NullDataError("Not provided price");

        if (dto.getPrice().compareTo(new BigDecimal(0)) < 1)
            throw new NullDataError("Price must be greather than 0");

        Optional<City> cityOpt = cityRepository.findById(dto.getLocation());
        if (cityOpt.isEmpty())
            throw new PropertyError("Not found city with id " + dto.getLocation().toString(), 404, "NotFound");

        City city = cityOpt.get();

        // * if is in Cali || Bogota price must be greather than 2'000.000
        if ((city.getName().equals("Bogota") || city.getName().equals("Cali"))
                && dto.getPrice().compareTo(new BigDecimal(2000000)) < 1)
            throw new PropertyError("Price must be > 2'000.000 in this city", 400, "BadRequest");

        res.setImg(dto.getImage());
        res.setLocation(city);
        res.setName(dto.getName());

        return res;
    }
}
