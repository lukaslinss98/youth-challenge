package com.youth.wearables.usermanagement.infrastructure.security;

import com.youth.wearables.usermanagement.application.security.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
class BCryptPasswordHasher implements PasswordHasher {

  private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

  @Override
  public String hash(String rawPassword) {
    return bCryptPasswordEncoder.encode(rawPassword);
  }

  @Override
  public boolean matches(String rawPassword, String passwordHash) {
    return bCryptPasswordEncoder.matches(rawPassword, passwordHash);
  }
}
