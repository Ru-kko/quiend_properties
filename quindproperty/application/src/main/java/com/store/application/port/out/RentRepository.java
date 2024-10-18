package com.store.application.port.out;

import com.store.domain.table.Rent;

public interface RentRepository {
  Rent save(Rent toSave);
}
