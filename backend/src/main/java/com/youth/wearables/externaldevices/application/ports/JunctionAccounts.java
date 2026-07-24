package com.youth.wearables.externaldevices.application.ports;

import com.youth.wearables.externaldevices.domain.AccountNotProvisionedException;
import java.util.Optional;
import java.util.UUID;

public interface JunctionAccounts {

  boolean exists(UUID userId);

  Optional<UUID> junctionUserId(UUID userId);

  Optional<UUID> userIdByJunctionUserId(UUID junctionUserId);

  void link(UUID userId, UUID junctionUserId);

  default UUID requireJunctionUserId(UUID userId) {
    return junctionUserId(userId).orElseThrow(() -> new AccountNotProvisionedException(userId));
  }
}
