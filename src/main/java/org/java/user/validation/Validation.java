package org.java.user.validation;

import java.util.function.Function;

public interface Validation<T> extends Function<T, ValidationResult> {

  default Validation<T> and(Validation<T> other) {
    return input -> {
      ValidationResult result = this.apply(input);
      return result.isValid()
          ? other.apply(input)
          : result.and(other.apply(input));
    };
  }

  default Validation<T> or(Validation<T> other) {
    return input -> {
      ValidationResult result = this.apply(input);
      return result.isValid()
          ? result
          : other.apply(input);
    };
  }

  static <T> Validation<T> when(boolean condition, Validation<T> validation) {
    return input -> condition
        ? validation.apply(input)
        : ValidationResult.valid();
  }
}
