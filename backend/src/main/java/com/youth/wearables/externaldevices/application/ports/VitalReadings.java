package com.youth.wearables.externaldevices.application.ports;

import com.youth.wearables.externaldevices.domain.VitalReading;
import java.util.List;
import java.util.UUID;

public interface VitalReadings {

  void saveAll(UUID deviceConnectionId, List<VitalReading> readings);
}
