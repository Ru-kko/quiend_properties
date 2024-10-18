package com.store.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.store.domain.table.City;

public interface CityRepository {
  Optional<City> findById(UUID id);
}
