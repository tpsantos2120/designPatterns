package org.java.user.validation;

import java.util.Map;
import java.util.Optional;
import org.java.user.validation.impl.Invalid;
import org.java.user.validation.impl.Valid;

public interface ValidationResult {

  static ValidationResult valid() {
    return new Valid();
  }

  static ValidationResult invalid(String code, String message) {
    return new Invalid(Map.of(code, message));
  }

  static ValidationResult invalid(Map<String, String> reasons) {
    return new Invalid(reasons);
  }

  boolean isValid();

  Optional<Map<String, String>> getReasons();

  ValidationResult and(ValidationResult other);

  ValidationResult or(ValidationResult other);
}
