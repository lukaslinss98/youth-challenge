package com.youth.wearables.junction;

import com.junction.api.Junction;
import com.junction.api.core.ApiError;
import com.junction.api.resources.user.requests.UserCreateBody;
import com.junction.api.types.ClientFacingUser;
import com.youth.wearables.usermanagement.application.provisioning.WearableAccountProvisioner;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
class JunctionGateway implements WearableAccountProvisioner {

  private static final Logger log = LoggerFactory.getLogger(JunctionGateway.class);

  private final Junction junction;
  private final JunctionAccountRepository accounts;

  JunctionGateway(Junction junction, JunctionAccountRepository accounts) {
    this.junction = junction;
    this.accounts = accounts;
  }

  @Async
  @Override
  public void ensureProvisioned(UUID userId) {
    if (accounts.existsById(userId)) {
      return;
    }
    try {
      ClientFacingUser created =
          junction.user().create(UserCreateBody.builder().clientUserId(userId.toString()).build());
      accounts.save(
          new JunctionAccountV1(
              userId, UUID.fromString(created.getUserId()), LocalDateTime.now()));
      log.info("Provisioned Junction account for user {}", userId);
    } catch (ApiError e) {
      log.error(
          "Failed to provision Junction account for user {} (status {})",
          userId,
          e.statusCode(),
          e);
    }
  }
}
