package com.youth.wearables.externaldevices.application;

import com.youth.wearables.externaldevices.application.ports.JunctionAccounts;
import com.youth.wearables.externaldevices.application.ports.JunctionApi;
import com.youth.wearables.externaldevices.domain.AccountNotProvisionedException;
import com.youth.wearables.externaldevices.domain.LinkToken;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DeviceConnectionService {

  private final JunctionAccounts accounts;
  private final JunctionApi junctionApi;

  DeviceConnectionService(JunctionAccounts accounts, JunctionApi junctionApi) {
    this.accounts = accounts;
    this.junctionApi = junctionApi;
  }

  public LinkToken createLinkToken(UUID userId) {
    UUID junctionUserId =
        accounts
            .junctionUserId(userId)
            .orElseThrow(() -> new AccountNotProvisionedException(userId));
    return junctionApi.createLinkToken(junctionUserId);
  }
}
