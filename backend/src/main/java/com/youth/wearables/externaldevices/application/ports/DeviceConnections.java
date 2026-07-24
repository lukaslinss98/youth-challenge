package com.youth.wearables.externaldevices.application.ports;

import com.youth.wearables.externaldevices.domain.ConnectionStatus;
import com.youth.wearables.externaldevices.domain.DeviceConnection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceConnections {

  void upsert(UUID userId, String providerSlug, ConnectionStatus status);

  void updateStatus(UUID userId, String providerSlug, ConnectionStatus status);

  void markPending(UUID userId, String providerSlug);

  int expirePending(LocalDateTime olderThan);

  List<DeviceConnection> forUser(UUID userId);

  Optional<UUID> connectionId(UUID userId, String providerSlug);
}
