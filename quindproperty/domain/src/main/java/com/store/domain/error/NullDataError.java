package com.store.domain.error;

public class NullDataError extends PropertyError {
  public NullDataError(String message) {
    super(message, 400, "BadRequest");
  }

  public NullDataError(String message, Throwable cause) {
    super(message, cause, 400, "BadRequest");
  }
}
