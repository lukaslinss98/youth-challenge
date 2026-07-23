package com.youth.wearables.externaldevices.domain;

import java.util.UUID;

public class AccountNotProvisionedException extends RuntimeException {

  public AccountNotProvisionedException(UUID userId) {
    super("No wearable account provisioned for user " + userId);
  }
}
