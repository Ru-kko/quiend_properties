package com.store.error;

import lombok.Getter;

public class NullDataError extends PropertyError {
  @Getter
  private final String Name = "MissingData";

  public NullDataError(String message) {
    super(message, 400, "BadRequest");
  }

  public NullDataError(String message, Throwable cause) {
    super(message, cause, 400, "BadRequest");
  }
}
