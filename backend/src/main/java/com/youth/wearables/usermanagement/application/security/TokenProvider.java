package com.youth.wearables.usermanagement.application.security;

import java.util.UUID;

public interface TokenProvider {

  String issueToken(UUID userId);
}
