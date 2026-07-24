package com.youth.wearables.externaldevices.application;

import com.youth.wearables.externaldevices.application.ports.DeviceConnections;
import com.youth.wearables.externaldevices.application.ports.JunctionAccounts;
import com.youth.wearables.externaldevices.domain.ConnectionStatus;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceConnectionEventService {

  private static final Logger log = LoggerFactory.getLogger(DeviceConnectionEventService.class);

  private final JunctionAccounts accounts;
  private final DeviceConnections deviceConnections;

  DeviceConnectionEventService(JunctionAccounts accounts, DeviceConnections deviceConnections) {
    this.accounts = accounts;
    this.deviceConnections = deviceConnections;
  }

  @Transactional
  public void handleConnectionCreated(UUID junctionUserId, String providerSlug) {
    accounts
        .userIdByJunctionUserId(junctionUserId)
        .ifPresentOrElse(
            userId -> deviceConnections.upsert(userId, providerSlug, ConnectionStatus.CONNECTED),
            () -> log.warn("Ignoring connection event for unknown Junction user {}", junctionUserId));
  }
}
