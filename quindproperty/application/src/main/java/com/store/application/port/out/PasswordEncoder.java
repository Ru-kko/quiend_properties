package com.store.application.port.out;

public interface PasswordEncoder {
  String encode(String plainPassword);
}
