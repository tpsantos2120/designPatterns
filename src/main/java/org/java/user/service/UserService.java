package org.java.user.service;

import java.util.Map;
import org.java.user.data.UserRepository;
import org.java.user.dto.UserDto;
import org.java.user.exception.ValidationException;
import org.java.user.model.UserModel;
import org.java.user.validation.UserValidation;
import org.java.user.validation.Validation;
import org.java.user.validation.ValidationResult;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserDto createUser(UserDto userDto) {

    // Example 1: Using all() - validates all fields
    ValidationResult validationWithAll = UserValidation.all().apply(userDto);

    // Example 2: Using and() - chain multiple validations (all must pass)
    ValidationResult validationWithAnd = UserValidation.isFirstNameValid()
        .and(UserValidation.isLastNameValid())
        .and(UserValidation.isEmailValid())
        .and(UserValidation.isAgeValid())
        .apply(userDto);

    // Example 3: Using or() - alternative validations (at least one must pass)
    // Validates if either phone OR email is provided (both optional but need at least one)
    ValidationResult validationWithOr = UserValidation.required()
        .and(UserValidation.isPhoneValid().or(UserValidation.isEmailValid()))
        .apply(userDto);

    // Example 4: Using when() - conditional validation
    // Only validate age if it's provided
    ValidationResult validationWithWhen = UserValidation.required()
        .and(Validation.when(userDto.age() != null, UserValidation.isAgeValid()))
        .and(Validation.when(userDto.phone() != null, UserValidation.isPhoneValid()))
        .apply(userDto);

    // Example 5: Complex combination - mixing and(), or(), when()
    // Required fields + conditional optional fields + either phone or address
    ValidationResult validationWithAndOrWhen = UserValidation.required()
        .and(Validation.when(userDto.age() != null, UserValidation.isAgeValid()))
        .and(UserValidation.isPhoneValid().or(UserValidation.isAddressValid()))
        .apply(userDto);

    if (!validationWithAll.isValid()) {
      throw new ValidationException(validationWithAll.getReasons().orElse(Map.of()));
    }

    UserModel userModel = mapDtoToEntity(userDto);
    UserModel savedUserModel = userRepository.save(userModel);
    return mapDtoToDto(savedUserModel);
  }

  private UserDto mapDtoToDto(UserModel userModel) {
    return new UserDto(
        userModel.id(),
        userModel.firstName(),
        userModel.lastName(),
        userModel.email(),
        userModel.age(),
        userModel.phone(),
        userModel.address()
    );
  }

  private UserModel mapDtoToEntity(UserDto userDto) {
    return UserModel.builder()
        .firstName(userDto.firstName())
        .lastName(userDto.lastName())
        .age(userDto.age())
        .email(userDto.email())
        .address(userDto.address())
        .phone(userDto.phone())
        .build();
  }
}