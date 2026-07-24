package com.youth.wearables.externaldevices.application;

import com.youth.wearables.externaldevices.application.ports.DeviceConnections;
import com.youth.wearables.externaldevices.application.ports.JunctionAccounts;
import com.youth.wearables.externaldevices.application.ports.JunctionApi;
import com.youth.wearables.externaldevices.domain.AccountNotProvisionedException;
import com.youth.wearables.externaldevices.domain.ConnectionStatus;
import com.youth.wearables.externaldevices.domain.DeviceConnection;
import com.youth.wearables.externaldevices.domain.LinkToken;
import com.youth.wearables.externaldevices.domain.WearableProvider;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DeviceConnectionService {

  private final JunctionAccounts accounts;
  private final JunctionApi junctionApi;
  private final DeviceConnections deviceConnections;
  private final VitalSyncService vitalSyncService;

  DeviceConnectionService(
      JunctionAccounts accounts,
      JunctionApi junctionApi,
      DeviceConnections deviceConnections,
      VitalSyncService vitalSyncService) {
    this.accounts = accounts;
    this.junctionApi = junctionApi;
    this.deviceConnections = deviceConnections;
    this.vitalSyncService = vitalSyncService;
  }

  public LinkToken createLinkToken(UUID userId, WearableProvider provider) {
    UUID junctionUserId =
        accounts
            .junctionUserId(userId)
            .orElseThrow(() -> new AccountNotProvisionedException(userId));
    LinkToken linkToken = junctionApi.createLinkToken(junctionUserId, provider);
    deviceConnections.markPending(userId, provider.junctionSlug());
    return linkToken;
  }

  public void connectDemo(UUID userId, WearableProvider provider) {
    UUID junctionUserId =
        accounts
            .junctionUserId(userId)
            .orElseThrow(() -> new AccountNotProvisionedException(userId));
    junctionApi.connectDemo(junctionUserId, provider);
    deviceConnections.upsert(userId, provider.slug(), ConnectionStatus.CONNECTED);
    vitalSyncService.syncUserAsync(userId, provider.slug());
  }

  public void disconnect(UUID userId, String providerSlug) {
    UUID junctionUserId =
        accounts
            .junctionUserId(userId)
            .orElseThrow(() -> new AccountNotProvisionedException(userId));
    junctionApi.deregisterProvider(junctionUserId, providerSlug);
    deviceConnections.updateStatus(userId, providerSlug, ConnectionStatus.DISCONNECTED);
  }

  public List<DeviceConnection> listConnections(UUID userId) {
    return deviceConnections.forUser(userId);
  }
}
