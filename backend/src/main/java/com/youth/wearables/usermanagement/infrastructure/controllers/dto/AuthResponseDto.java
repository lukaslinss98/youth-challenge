package com.youth.wearables.usermanagement.infrastructure.controllers.dto;

public record AuthResponseDto(UserDto user) {

  public record UserDto(String id, String email) {}
}
