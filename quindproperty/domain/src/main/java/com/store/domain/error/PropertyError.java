package com.store.domain.error;

import lombok.Getter;

@Getter
public class PropertyError extends Exception {
  private final Integer code;
  private final String status;

  public PropertyError(String message, Integer code, String status) {
    super(message);
    this.code = code;
    this.status = status;
  }
  
  public PropertyError(String message, Throwable cause, Integer code, String status) {
    super(message, cause);
    this.code = code;
    this.status = status;
  }

  public static PropertyError badRequest(String message, Throwable cause) {
    return new PropertyError(message, cause, 400, "BadRequest");
  }
}
