package org.java.user.controllers;

import org.java.user.service.UserService;
import org.java.user.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

  private final UserService userService;

  public Controller(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/api/users")
  public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));
  }
}
