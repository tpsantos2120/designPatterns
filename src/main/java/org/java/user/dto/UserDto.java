package org.java.user.dto;

import java.util.UUID;

public record UserDto(
    UUID userId,
    String firstName,
    String lastName,
    String email,
    Integer age,
    String phone,
    String address
) {
}