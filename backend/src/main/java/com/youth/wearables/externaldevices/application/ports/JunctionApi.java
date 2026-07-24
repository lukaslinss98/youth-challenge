package com.youth.wearables.externaldevices.application.ports;

import com.youth.wearables.externaldevices.domain.LinkToken;
import com.youth.wearables.externaldevices.domain.WearableProvider;
import java.util.UUID;

public interface JunctionApi {

  UUID createUser(UUID clientUserId);

  LinkToken createLinkToken(UUID junctionUserId, WearableProvider provider);
}
