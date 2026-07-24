package com.youth.wearables.externaldevices.application.ports;

import com.youth.wearables.externaldevices.domain.ConnectionStatus;
import com.youth.wearables.externaldevices.domain.DeviceConnection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceConnections {

  void upsert(UUID userId, String providerSlug, ConnectionStatus status);

  List<DeviceConnection> forUser(UUID userId);

  Optional<UUID> connectionId(UUID userId, String providerSlug);
}
