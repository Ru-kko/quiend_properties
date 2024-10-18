package com.store.infrastructure.persistnce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.store.domain.table.Property;

import java.util.UUID;
import java.math.BigDecimal;

@Repository
public interface JPAPropertyRepository extends JpaRepository<Property, UUID> {
  @Query("SELECT p FROM Property p WHERE p.price >= :lowerPrice AND p.available = true AND p.active = true")
  Page<Property> findAllWithLowerPrice(@Param("lowerPrice") BigDecimal lowerPrice, Pageable pageable);

  @Query("SELECT p FROM Property p WHERE p.price >= :lowerPrice AND p.price <= :upperPrice AND p.available = true AND p.active = true")
  Page<Property> findAllWithPriceRange(@Param("lowerPrice") BigDecimal lowerPrice,
      @Param("upperPrice") BigDecimal upperPrice, Pageable pageable);
}
