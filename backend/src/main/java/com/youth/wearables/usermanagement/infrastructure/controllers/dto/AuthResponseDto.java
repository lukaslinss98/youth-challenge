package com.youth.wearables.usermanagement.infrastructure.controllers.dto;

public record AuthResponseDto(UserDto user, String token) {

  public record UserDto(String id, String email, String username) {}
}
