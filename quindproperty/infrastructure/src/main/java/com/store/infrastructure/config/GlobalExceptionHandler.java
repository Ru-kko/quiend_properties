package com.store.infrastructure.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.store.domain.error.PropertyError;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(PropertyError.class)
  public ResponseEntity<Map<String, Object>> handlePropertyError(PropertyError e) {
    Map<String, Object> body = new HashMap<>();
    body.put("message", e.getMessage());
    body.put("code", e.getCode());
    body.put("status", e.getStatus());

    return new ResponseEntity<>(body, HttpStatus.valueOf(e.getCode()));
  }
}