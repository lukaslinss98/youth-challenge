package com.youth.wearables.externaldevices.application;

import com.youth.wearables.externaldevices.application.ports.JunctionAccounts;
import com.youth.wearables.externaldevices.application.ports.JunctionApi;
import com.youth.wearables.externaldevices.domain.JunctionApiException;
import com.youth.wearables.usermanagement.application.provisioning.WearableAccountProvisioner;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
class WearableAccountProvisioningService implements WearableAccountProvisioner {

  private static final Logger log =
      LoggerFactory.getLogger(WearableAccountProvisioningService.class);

  private final JunctionAccounts accounts;
  private final JunctionApi junctionApi;

  WearableAccountProvisioningService(JunctionAccounts accounts, JunctionApi junctionApi) {
    this.accounts = accounts;
    this.junctionApi = junctionApi;
  }

  @Async
  @Override
  public void ensureProvisioned(UUID userId) {
    if (accounts.exists(userId)) {
      return;
    }
    try {
      UUID junctionUserId = junctionApi.createUser(userId);
      accounts.link(userId, junctionUserId);
      log.info("Provisioned Junction account for user {}", userId);
    } catch (JunctionApiException e) {
      log.error("Failed to provision Junction account for user {}", userId, e);
    }
  }
}
