package com.youth.wearables.externaldevices.infrastructure.persistence;

import com.youth.wearables.externaldevices.application.ports.VitalReadings;
import com.youth.wearables.externaldevices.domain.VitalMetric;
import com.youth.wearables.externaldevices.domain.VitalReading;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
class VitalReadingRepositoryImpl implements VitalReadings {

  private final VitalReadingJpaRepository jpaRepository;

  VitalReadingRepositoryImpl(VitalReadingJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Map<String, List<VitalReading>> latestPerMetricByProvider(UUID userId) {
    return jpaRepository.findLatestPerMetricForUser(userId).stream()
        .collect(
            Collectors.groupingBy(
                VitalReadingProjection::getProvider,
                Collectors.mapping(
                    p ->
                        new VitalReading(
                            VitalMetric.valueOf(p.getMetric()),
                            p.getMeasuredAt().atOffset(ZoneOffset.UTC),
                            p.getValue(),
                            p.getUnit()),
                    Collectors.toList())));
  }

  @Override
  public int saveAll(UUID deviceConnectionId, List<VitalReading> readings) {
    if (readings.isEmpty()) {
      return 0;
    }

    int size = readings.size();
    UUID[] ids = new UUID[size];
    UUID[] connectionIds = new UUID[size];
    String[] metrics = new String[size];
    OffsetDateTime[] measuredAts = new OffsetDateTime[size];
    Double[] values = new Double[size];
    String[] units = new String[size];
    OffsetDateTime[] createdAts = new OffsetDateTime[size];
    OffsetDateTime now = OffsetDateTime.now();

    for (int i = 0; i < size; i++) {
      VitalReading reading = readings.get(i);
      ids[i] = UUID.randomUUID();
      connectionIds[i] = deviceConnectionId;
      metrics[i] = reading.metric().name();
      measuredAts[i] = reading.measuredAt();
      values[i] = reading.value();
      units[i] = reading.unit();
      createdAts[i] = now;
    }

    return jpaRepository.batchInsertIgnore(
        ids, connectionIds, metrics, measuredAts, values, units, createdAts);
  }
}
