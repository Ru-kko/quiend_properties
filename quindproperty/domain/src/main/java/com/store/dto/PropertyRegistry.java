package com.store.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyRegistry {
  private String name;
  private UUID location;
  private String image;
  private BigDecimal price;
}
