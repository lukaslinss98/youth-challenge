package com.youth.wearables.usermanagement.domain;

public sealed interface LoginResult permits LoginResult.Success, LoginResult.InvalidCredentials {

  record Success(String userId, String email, String token) implements LoginResult {}

  record InvalidCredentials() implements LoginResult {}
}
