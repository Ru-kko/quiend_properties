package com.store.domain.dto;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CleanRent {
  private UUID rentID;
  private Date rentDate;
  private CleanProperty property;
  private UserClaims user;
}
