package org.java.user.service;

import org.java.user.data.UserRepository;
import org.java.user.dto.UserDto;
import org.java.user.exception.ValidationException;
import org.java.user.model.UserModel;
import org.java.user.validation.UserValidation;
import org.java.user.validation.ValidationResult;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserDto createUser(UserDto userDto) {
    // Apply combinator pattern validation
    ValidationResult validationResult = UserValidation.all().apply(userDto);

    if (!validationResult.isValid()) {
      throw new ValidationException(validationResult.getReasons().orElse(java.util.Map.of()));
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