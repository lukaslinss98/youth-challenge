package com.youth.wearables.externaldevices.application;

import com.youth.wearables.externaldevices.application.ports.VitalReadings;
import com.youth.wearables.externaldevices.domain.VitalReading;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class VitalQueryService {

  private final VitalReadings vitalReadings;

  VitalQueryService(VitalReadings vitalReadings) {
    this.vitalReadings = vitalReadings;
  }

  public Map<String, List<VitalReading>> latestReadingsByProvider(UUID userId) {
    return vitalReadings.latestPerMetricByProvider(userId);
  }
}
