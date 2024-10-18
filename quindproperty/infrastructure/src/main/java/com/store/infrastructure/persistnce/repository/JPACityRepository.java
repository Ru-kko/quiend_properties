package com.store.infrastructure.persistnce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.store.application.port.out.CityRepository;
import com.store.domain.table.City;

import java.util.UUID;

@Repository
public interface JPACityRepository extends JpaRepository<City, UUID>, CityRepository { 
}
