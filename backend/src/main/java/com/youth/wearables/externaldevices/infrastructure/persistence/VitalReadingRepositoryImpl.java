package com.youth.wearables.externaldevices.infrastructure.persistence;

import com.youth.wearables.externaldevices.application.ports.VitalReadings;
import com.youth.wearables.externaldevices.domain.VitalMetric;
import com.youth.wearables.externaldevices.domain.VitalReading;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
class VitalReadingRepositoryImpl implements VitalReadings {

  private final VitalReadingJpaRepository jpaRepository;

  VitalReadingRepositoryImpl(VitalReadingJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  @Transactional
  public void saveAll(UUID deviceConnectionId, List<VitalReading> readings) {
    if (readings.isEmpty()) {
      return;
    }

    OffsetDateTime now = OffsetDateTime.now();
    List<VitalReadingV1> toInsert = new ArrayList<>();

    Map<VitalMetric, List<VitalReading>> byMetric =
        readings.stream().collect(Collectors.groupingBy(VitalReading::metric));

    byMetric.forEach(
        (metric, metricReadings) -> {
          OffsetDateTime min =
              metricReadings.stream()
                  .map(VitalReading::measuredAt)
                  .min(Comparator.naturalOrder())
                  .orElseThrow();
          OffsetDateTime max =
              metricReadings.stream()
                  .map(VitalReading::measuredAt)
                  .max(Comparator.naturalOrder())
                  .orElseThrow();

          Set<OffsetDateTime> existing =
              new HashSet<>(
                  jpaRepository.findMeasuredAt(deviceConnectionId, metric.name(), min, max));

          for (VitalReading reading : metricReadings) {
            if (existing.add(reading.measuredAt())) {
              toInsert.add(
                  new VitalReadingV1(
                      UUID.randomUUID(),
                      deviceConnectionId,
                      metric.name(),
                      reading.measuredAt(),
                      reading.value(),
                      reading.unit(),
                      now));
            }
          }
        });

    jpaRepository.saveAll(toInsert);
  }
}
