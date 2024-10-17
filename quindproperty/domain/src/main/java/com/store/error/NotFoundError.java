package com.store.error;

public class NotFoundError extends PropertyError {
  public NotFoundError(String message) {
    super(message, 404, "NotFound");
  }
}
