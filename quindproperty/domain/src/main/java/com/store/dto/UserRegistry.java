package com.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistry {
  private String email;
  private String password;
  private String firstName;
  private String lastName;
  private Integer age;
}
