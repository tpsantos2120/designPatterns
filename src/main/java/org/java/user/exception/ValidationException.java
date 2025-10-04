package org.java.user.exception;

import java.util.Map;
import java.util.stream.Collectors;

public class ValidationException extends RuntimeException {

  private final Map<String, String> validationErrors;

  public ValidationException(Map<String, String> validationErrors) {
    super("Validation failed: " + validationErrors.entrySet().stream()
        .map(entry -> entry.getKey() + ": " + entry.getValue())
        .collect(Collectors.joining(", ")));
    this.validationErrors = validationErrors;
  }

  public Map<String, String> getValidationErrors() {
    return validationErrors;
  }
}
