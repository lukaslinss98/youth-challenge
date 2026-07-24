package com.youth.wearables.externaldevices.application.ports;

import java.util.Optional;
import java.util.UUID;

public interface JunctionAccounts {

  boolean exists(UUID userId);

  Optional<UUID> junctionUserId(UUID userId);

  Optional<UUID> userIdByJunctionUserId(UUID junctionUserId);

  void link(UUID userId, UUID junctionUserId);
}
