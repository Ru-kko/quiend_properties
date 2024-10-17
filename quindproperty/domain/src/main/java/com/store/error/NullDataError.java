package com.store.error;

import lombok.Getter;

@Getter
public class NullDataError extends PropertyError {
  private final String name = "MissingData";

  public NullDataError(String message) {
    super(message, 400, "BadRequest");
  }

  public NullDataError(String message, Throwable cause) {
    super(message, cause, 400, "BadRequest");
  }
}
