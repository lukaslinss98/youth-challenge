package com.youth.wearables.externaldevices.application.ports;

import com.youth.wearables.externaldevices.domain.VitalReading;
import com.youth.wearables.externaldevices.domain.VitalResource;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface WearableVitalsApi {

  List<VitalReading> fetch(
      UUID junctionUserId, String providerSlug, VitalResource resource, LocalDate start, LocalDate end);
}
