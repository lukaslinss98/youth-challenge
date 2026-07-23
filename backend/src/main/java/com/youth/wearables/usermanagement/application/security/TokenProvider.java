package com.youth.wearables.usermanagement.application.security;

import java.util.Optional;
import java.util.UUID;

public interface TokenProvider {

  String issueToken(UUID userId);

  /** Verifies the token's signature and expiry, returning the user id if valid. */
  Optional<UUID> resolveUserId(String token);
}
