package com.youth.wearables.usermanagement.application;

public sealed interface RegistrationResult
    permits RegistrationResult.UserRegistered, RegistrationResult.UserExists {

  record UserRegistered(String userId, String email) implements RegistrationResult {}

  record UserExists() implements RegistrationResult {}
}
