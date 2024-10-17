package com.store.dto;

import java.util.UUID;

import com.store.domain.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserClaims {
  private UUID userId;
  private String email;
  private String lastName;
  private Role role;
}
