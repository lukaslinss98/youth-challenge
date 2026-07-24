package com.youth.wearables.externaldevices.application;

import com.youth.wearables.externaldevices.application.ports.DeviceConnections;
import com.youth.wearables.externaldevices.application.ports.JunctionAccounts;
import com.youth.wearables.externaldevices.application.ports.JunctionApi;
import com.youth.wearables.externaldevices.domain.AccountNotProvisionedException;
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

  DeviceConnectionService(
      JunctionAccounts accounts, JunctionApi junctionApi, DeviceConnections deviceConnections) {
    this.accounts = accounts;
    this.junctionApi = junctionApi;
    this.deviceConnections = deviceConnections;
  }

  public LinkToken createLinkToken(UUID userId, WearableProvider provider) {
    UUID junctionUserId =
        accounts
            .junctionUserId(userId)
            .orElseThrow(() -> new AccountNotProvisionedException(userId));
    return junctionApi.createLinkToken(junctionUserId, provider);
  }

  public List<DeviceConnection> listConnections(UUID userId) {
    return deviceConnections.forUser(userId);
  }
}
