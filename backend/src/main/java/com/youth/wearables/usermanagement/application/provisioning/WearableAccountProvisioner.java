package com.youth.wearables.usermanagement.application.provisioning;

import java.util.UUID;

public interface WearableAccountProvisioner {

  void ensureProvisioned(UUID userId);
}
