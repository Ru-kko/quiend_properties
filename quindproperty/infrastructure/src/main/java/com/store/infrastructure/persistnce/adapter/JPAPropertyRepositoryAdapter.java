package com.store.infrastructure.persistnce.adapter;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.store.application.port.out.PropertyRepository;
import com.store.domain.error.PropertyError;
import com.store.domain.table.Property;
import com.store.infrastructure.persistnce.repository.JPAPropertyRepository;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class JPAPropertyRepositoryAdapter implements PropertyRepository {
  private JPAPropertyRepository propertyRepository;

  public Page<Property> findAllWithLowerPrice(BigDecimal lowerPrice, Pageable pageable) {
    return propertyRepository.findAllWithLowerPrice(lowerPrice, pageable);
  }

  @Override
  public Page<Property> findAllWithPriceRange(BigDecimal lowerPrice, BigDecimal upperPrice, Pageable pageable) {
    return propertyRepository.findAllWithPriceRange(lowerPrice, upperPrice, pageable);
  }

  @Override
  public Property save(Property property) throws PropertyError {
    try {
      return propertyRepository.save(property);
    } catch (DataIntegrityViolationException e) {
      handleConstraintViolation(e);
      return null; // Este return no se ejecutará porque se lanza la excepción
    }
  }

  @Override
  public Optional<Property> findById(UUID id) {
    return propertyRepository.findById(id);
  }

  private void handleConstraintViolation(DataIntegrityViolationException e) throws PropertyError {
    Throwable rootCause = e.getRootCause();
    if (rootCause instanceof org.hibernate.exception.ConstraintViolationException) {
      String sqlState = ((org.hibernate.exception.ConstraintViolationException) rootCause).getSQLState();
      if ("23505".equals(sqlState)) { // Código SQL para constraint de clave única en Postgres
        throw PropertyError.badRequest("A property with this name already exists", e);
      }
    }
    throw PropertyError.badRequest("Database constraint violation occurred", e);
  }
}
