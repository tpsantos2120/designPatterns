package org.java.user.validation.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.java.user.validation.ValidationResult;

public record Invalid(Map<String, String> reasons) implements ValidationResult {

  @Override
  public boolean isValid() {
    return false;
  }

  @Override
  public Optional<Map<String, String>> getReasons() {
    return Optional.of(reasons);
  }

  @Override
  public ValidationResult and(ValidationResult other) {
    if (other instanceof Valid) {
      return this;
    }
    Map<String, String> combinedReasons = new HashMap<>(this.reasons);
    combinedReasons.putAll(other.getReasons().orElse(Map.of()));
    return new Invalid(combinedReasons);
  }

  @Override
  public ValidationResult or(ValidationResult other) {
    return other.isValid() ? other : this;
  }
}
