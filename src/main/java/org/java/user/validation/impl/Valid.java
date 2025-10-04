package org.java.user.validation.impl;

import java.util.Map;
import java.util.Optional;
import org.java.user.validation.ValidationResult;

public record Valid() implements ValidationResult {

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public Optional<Map<String, String>> getReasons() {
    return Optional.empty();
  }

  @Override
  public ValidationResult and(ValidationResult other) {
    return other;
  }

  @Override
  public ValidationResult or(ValidationResult other) {
    return this;
  }
}
