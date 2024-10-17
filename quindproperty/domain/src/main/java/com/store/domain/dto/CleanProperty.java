package com.store.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.store.domain.table.City;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CleanProperty {
  private UUID propertyId;
  private Boolean available;
  private String image;
  private BigDecimal price;
  private City location;
}
