package org.java.user.validation;

import java.util.Objects;
import java.util.regex.Pattern;
import org.java.user.dto.UserDto;

public interface UserValidation extends Validation<UserDto> {

  static UserValidation isEmailValid() {
    return userDto -> {
      String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
      Pattern pattern = Pattern.compile(emailRegex);
      return Objects.nonNull(userDto.email()) && pattern.matcher(userDto.email()).matches()
          ? ValidationResult.valid()
          : ValidationResult.invalid(
          ErrorCode.INVALID_EMAIL.getCode(),
          ErrorCode.INVALID_EMAIL.getMessage());
    };
  }

  static UserValidation isFirstNameValid() {
    return userDto -> userDto.firstName() != null && !userDto.firstName().trim().isEmpty()
        ? ValidationResult.valid()
        : ValidationResult.invalid(
        ErrorCode.INVALID_FIRST_NAME.getCode(),
        ErrorCode.INVALID_FIRST_NAME.getMessage());
  }

  static UserValidation isLastNameValid() {
    return userDto -> userDto.lastName() != null && !userDto.lastName().trim().isEmpty()
        ? ValidationResult.valid()
        : ValidationResult.invalid(
        ErrorCode.INVALID_LAST_NAME.getCode(),
        ErrorCode.INVALID_LAST_NAME.getMessage());
  }

  static UserValidation isAgeValid() {
    return userDto -> {
      if (Objects.isNull(userDto.age())) {
        return ValidationResult.valid(); // Age is optional
      }
      return userDto.age() >= 18 && userDto.age() <= 120
          ? ValidationResult.valid()
          : ValidationResult.invalid(
          ErrorCode.INVALID_AGE.getCode(),
          ErrorCode.INVALID_AGE.getMessage());
    };
  }

  static UserValidation isPhoneValid() {
    return userDto -> {
      if (Objects.isNull(userDto.phone()) || userDto.phone().trim().isEmpty()) {
        return ValidationResult.valid(); // Phone is optional
      }
      String phoneRegex = "^\\+?[0-9]{10,15}$";
      Pattern pattern = Pattern.compile(phoneRegex);
      return pattern.matcher(userDto.phone().replaceAll("[\\s-]", "")).matches()
          ? ValidationResult.valid()
          : ValidationResult.invalid(
          ErrorCode.INVALID_PHONE.getCode(),
          ErrorCode.INVALID_PHONE.getMessage());
    };
  }

  static UserValidation isAddressValid() {
    return userDto -> {
      if (userDto.address() == null || userDto.address().trim().isEmpty()) {
        return ValidationResult.valid(); // Address is optional
      }
      return userDto.address().length() >= 10
          ? ValidationResult.valid()
          : ValidationResult.invalid(
          ErrorCode.INVALID_ADDRESS.getCode(),
          ErrorCode.INVALID_ADDRESS.getMessage());
    };
  }

  static UserValidation hasMinimumNameLength(int minLength) {
    return userDto -> {
      boolean firstNameValid =
          userDto.firstName() != null && userDto.firstName().length() >= minLength;
      boolean lastNameValid =
          userDto.lastName() != null && userDto.lastName().length() >= minLength;
      return firstNameValid && lastNameValid
          ? ValidationResult.valid()
          : ValidationResult.invalid(
          ErrorCode.INVALID_NAME_LENGTH.getCode(),
          ErrorCode.INVALID_NAME_LENGTH.getMessage(minLength));
    };
  }

  static Validation<UserDto> all() {
    return isFirstNameValid()
        .and(isLastNameValid())
        .and(isEmailValid())
        .and(isAgeValid())
        .and(isPhoneValid())
        .and(isAddressValid());
  }

  static Validation<UserDto> required() {
    return isFirstNameValid()
        .and(isLastNameValid())
        .and(isEmailValid());
  }
}
