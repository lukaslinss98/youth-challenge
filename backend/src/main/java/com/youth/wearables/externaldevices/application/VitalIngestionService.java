package com.youth.wearables.externaldevices.application;

import com.youth.wearables.externaldevices.application.ports.DeviceConnections;
import com.youth.wearables.externaldevices.application.ports.JunctionAccounts;
import com.youth.wearables.externaldevices.application.ports.VitalReadings;
import com.youth.wearables.externaldevices.application.ports.WearableVitalsApi;
import com.youth.wearables.externaldevices.domain.VitalReading;
import com.youth.wearables.externaldevices.domain.VitalResource;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class VitalIngestionService {

  private static final Logger log = LoggerFactory.getLogger(VitalIngestionService.class);

  private final JunctionAccounts accounts;
  private final DeviceConnections deviceConnections;
  private final WearableVitalsApi vitalsApi;
  private final VitalReadings vitalReadings;

  VitalIngestionService(
      JunctionAccounts accounts,
      DeviceConnections deviceConnections,
      WearableVitalsApi vitalsApi,
      VitalReadings vitalReadings) {
    this.accounts = accounts;
    this.deviceConnections = deviceConnections;
    this.vitalsApi = vitalsApi;
    this.vitalReadings = vitalReadings;
  }

  @Async("vitalIngestionExecutor")
  public void ingestHistorical(
      UUID junctionUserId, String providerSlug, LocalDate start, LocalDate end) {

    resolveConnection(junctionUserId, providerSlug)
        .ifPresent(
            connectionId -> {
              try {
                List<VitalReading> readings =
                    vitalsApi.fetchAll(junctionUserId, providerSlug, start, end);
                int inserted = vitalReadings.saveAll(connectionId, readings);
                log.info(
                    "Ingested {} new historical {} readings [{}..{}]",
                    inserted,
                    providerSlug,
                    start,
                    end);
              } catch (RuntimeException e) {
                log.error(
                    "Historical ingest failed for {} junctionUser {}",
                    providerSlug,
                    junctionUserId,
                    e);
              }
            });
  }

  @Async("vitalIngestionExecutor")
  public void ingestInline(
      UUID junctionUserId, String providerSlug, VitalResource resource, List<VitalReading> readings) {

    resolveConnection(junctionUserId, providerSlug)
        .ifPresent(
            connectionId -> {
              try {
                int inserted = vitalReadings.saveAll(connectionId, readings);
                log.info("Ingested {} new inline {} readings", inserted, resource.slug());
              } catch (RuntimeException e) {
                log.error(
                    "Inline ingest failed for {} junctionUser {}",
                    resource.slug(),
                    junctionUserId,
                    e);
              }
            });
  }

  private Optional<UUID> resolveConnection(UUID junctionUserId, String providerSlug) {
    Optional<UUID> userId = accounts.userIdByJunctionUserId(junctionUserId);
    if (userId.isEmpty()) {
      log.warn("Vital ingest skipped: unknown Junction user {}", junctionUserId);
      return Optional.empty();
    }

    Optional<UUID> connectionId = deviceConnections.connectionId(userId.get(), providerSlug);
    if (connectionId.isEmpty()) {
      log.warn("Vital ingest skipped: no {} connection for user {}", providerSlug, userId.get());
    }
    return connectionId;
  }
}
