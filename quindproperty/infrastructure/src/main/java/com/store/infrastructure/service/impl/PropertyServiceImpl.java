package com.store.infrastructure.service.impl;

import com.store.domain.table.City;
import com.store.domain.table.Property;
import com.store.dto.PropertyRegistry;
import com.store.error.NotFoundError;
import com.store.error.NullDataError;
import com.store.error.PropertyError;
import com.store.infrastructure.config.InfraProperties;
import com.store.infrastructure.persistence.CityRepository;
import com.store.infrastructure.persistence.PropertyRepository;
import com.store.infrastructure.service.PropertyService;

import lombok.AllArgsConstructor;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.Calendar;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
class PropertyServiceImpl implements PropertyService {
    private PropertyRepository propertyRepository;
    private InfraProperties props;
    private CityRepository cityRepository;

    private static final String NOT_FOUND_MESSAGE_PREF = "Not found a property with id " ;

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
        base.setAvailable(true);

        try {
            base = propertyRepository.save(base);
        } catch (DataIntegrityViolationException e) {
            isDuplicateNameViolation(e);
        }

        return base;
    }

    @Override
    public Property update(UUID id, PropertyRegistry newData) throws PropertyError {
        var originalOpt = propertyRepository.findById(id);

        if (originalOpt.isEmpty() || Boolean.TRUE.equals(!originalOpt.get().getActive()))
            throw new NotFoundError(NOT_FOUND_MESSAGE_PREF + id.toString());

        var original = originalOpt.get();

        if (Boolean.TRUE.equals(!original.getAvailable() && newData.getPrice() != null) && !newData.getPrice().equals(original.getPrice()))
            throw PropertyError.badRequest("Cant change the price of a currently rented property", null);
        if (!original.getAvailable() && newData.getLocation() != null
                && !newData.getLocation().equals(original.getLocation().getCityId()))
            throw PropertyError.badRequest("Cant change the location of a currently rented property", null);

        if (newData.getImage() == null)
            newData.setImage(original.getImg());
        if (newData.getName() == null)
            newData.setName(original.getName());
        if (newData.getPrice() == null)
            newData.setPrice(original.getPrice());
        if (newData.getLocation() == null)
            newData.setLocation(original.getLocation().getCityId());

        var modified = transform(newData, original);

        modified.setActive(original.getActive());

        Property response = null;
        
        try {
            response = propertyRepository.save(modified);
        } catch (DataIntegrityViolationException e) {
            isDuplicateNameViolation(e);
        }

        return response;
    }

    @Override
    public Property toggleAvailability(UUID id) throws PropertyError {
        var originalOpt = propertyRepository.findById(id);

        if (originalOpt.isEmpty() || Boolean.TRUE.equals(!originalOpt.get().getActive()))
            throw new NotFoundError(NOT_FOUND_MESSAGE_PREF + id.toString());

        var original = originalOpt.get();

        original.setAvailable(!original.getAvailable());

        propertyRepository.save(original);

        return original;
    }

    @Override
    public void delete(UUID id) throws PropertyError {
        var originalOpt = propertyRepository.findById(id);
        if (originalOpt.isEmpty() || Boolean.TRUE.equals(!originalOpt.get().getActive()))
            throw new NotFoundError(NOT_FOUND_MESSAGE_PREF + id.toString());
        

        var original = originalOpt.get();
        if (Boolean.FALSE.equals(original.getAvailable()))
            throw PropertyError.badRequest("Cant delete rented property", null);

        var dateCreated = Calendar.getInstance();
        dateCreated.setTime(original.getDateCreated());
            
        var now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        var diff = now.getTime().getTime() - dateCreated.getTime().getTime();
        if (diff > props.getTime2DeleteProperty())
            throw PropertyError.badRequest("Cant delete a property wtith more thant 1 month", null);
        
        original.setActive(false);

        propertyRepository.save(original);
    }

    /**
     * Checks if is repeated name error
     * ! this only works on postgres
     * 
     */
    private void isDuplicateNameViolation(DataIntegrityViolationException e) throws PropertyError {
        Throwable rootCause = e.getCause();
        if (!(rootCause instanceof ConstraintViolationException)) {
            throw e;
        }

        String sqlState = ((org.hibernate.exception.ConstraintViolationException) rootCause).getSQLState();
        if ("23505".equals(sqlState)) {
            throw PropertyError.badRequest("Already exists a property with this name", e);
        }

        throw e;
    }

    Property transform(PropertyRegistry dto) throws PropertyError {
        return transform(dto, new Property());
    }

    Property transform(PropertyRegistry dto, final Property base) throws PropertyError {
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
            throw new NotFoundError("Not found city with id " + dto.getLocation().toString());

        City city = cityOpt.get();

        // * if is in Cali || Bogota price must be greather than 2'000.000
        if ((city.getName().equals("Bogota") || city.getName().equals("Cali"))
                && dto.getPrice().compareTo(new BigDecimal(2000000)) < 1)
            throw PropertyError.badRequest("Price must be > 2'000.000 in this city", null);

        base.setImg(dto.getImage());
        base.setLocation(city);
        base.setPrice(dto.getPrice());
        base.setName(dto.getName());

        return base;
    }
}
