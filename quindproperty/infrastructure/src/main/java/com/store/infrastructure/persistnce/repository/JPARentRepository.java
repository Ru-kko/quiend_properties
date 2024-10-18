package com.store.infrastructure.persistnce.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.application.port.out.RentRepository;
import com.store.domain.table.Rent;

public interface JPARentRepository extends JpaRepository<Rent, UUID>, RentRepository {
}
