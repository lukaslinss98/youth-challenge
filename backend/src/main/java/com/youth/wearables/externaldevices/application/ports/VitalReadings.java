package com.youth.wearables.externaldevices.application.ports;

import com.youth.wearables.externaldevices.domain.VitalReading;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface VitalReadings {

  int saveAll(UUID deviceConnectionId, List<VitalReading> readings);

  Map<String, List<VitalReading>> latestPerMetricByProvider(UUID userId);
}
