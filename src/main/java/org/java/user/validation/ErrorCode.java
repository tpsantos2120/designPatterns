package org.java.user.validation;

public enum ErrorCode {
  INVALID_EMAIL("U001", "Email is not valid"),
  INVALID_FIRST_NAME("U002", "First name cannot be null or empty"),
  INVALID_LAST_NAME("U003", "Last name cannot be null or empty"),
  INVALID_AGE("U004", "Age must be between 18 and 120"),
  INVALID_PHONE("U005", "Phone number is not valid"),
  INVALID_ADDRESS("U006", "Address must be at least 10 characters long"),
  INVALID_NAME_LENGTH("U007", "First name and last name must be at least %d characters long");

  private final String code;
  private final String message;

  ErrorCode(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public String getMessage(Object... args) {
    return String.format(message, args);
  }
}
