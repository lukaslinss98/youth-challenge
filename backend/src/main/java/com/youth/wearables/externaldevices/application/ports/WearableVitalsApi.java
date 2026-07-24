package com.youth.wearables.externaldevices.application.ports;

import com.youth.wearables.externaldevices.domain.VitalReading;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface WearableVitalsApi {

  List<VitalReading> fetchAll(
      UUID junctionUserId, String providerSlug, LocalDate start, LocalDate end);
}
