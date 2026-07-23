package com.youth.wearables.usermanagement.domain;

public sealed interface RegistrationResult
    permits RegistrationResult.UserRegistered, RegistrationResult.UserExists {

  record UserRegistered(String userId, String email, String token) implements RegistrationResult {}

  record UserExists() implements RegistrationResult {}
}
