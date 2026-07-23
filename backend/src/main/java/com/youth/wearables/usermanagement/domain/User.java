package com.youth.wearables.usermanagement.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record User(
    UUID id, String email, String passwordHash, String junctionUserId, LocalDateTime createdAt) {

  public static User register(String email, String passwordHash) {
    return new User(UUID.randomUUID(), email, passwordHash, null, LocalDateTime.now());
  }

  public User withJunctionUserId(String junctionUserId) {
    return new User(id, email, passwordHash, junctionUserId, createdAt);
  }
}
