package com.youth.wearables.externaldevices.application.ports;

import java.util.Optional;
import java.util.UUID;

/** Persistence port for the internal-user to provider-user mapping. */
public interface JunctionAccounts {

  boolean exists(UUID userId);

  Optional<UUID> junctionUserId(UUID userId);

  void link(UUID userId, UUID junctionUserId);
}
