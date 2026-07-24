package com.youth.wearables.externaldevices.application;

import com.youth.wearables.externaldevices.application.ports.DeviceConnections;
import com.youth.wearables.externaldevices.application.ports.JunctionAccounts;
import com.youth.wearables.externaldevices.application.ports.VitalReadings;
import com.youth.wearables.externaldevices.application.ports.WearableVitalsApi;
import com.youth.wearables.externaldevices.domain.ConnectionStatus;
import com.youth.wearables.externaldevices.domain.DeviceConnection;
import com.youth.wearables.externaldevices.domain.JunctionApiException;
import com.youth.wearables.externaldevices.domain.VitalReading;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class VitalSyncService {

  private static final Logger log = LoggerFactory.getLogger(VitalSyncService.class);

  private final JunctionAccounts accounts;
  private final DeviceConnections deviceConnections;
  private final WearableVitalsApi vitalsApi;
  private final VitalReadings vitalReadings;
  private final int lookbackDays;

  VitalSyncService(
      JunctionAccounts accounts,
      DeviceConnections deviceConnections,
      WearableVitalsApi vitalsApi,
      VitalReadings vitalReadings,
      @Value("${vitals.sync.lookback-days:180}") int lookbackDays) {
    this.accounts = accounts;
    this.deviceConnections = deviceConnections;
    this.vitalsApi = vitalsApi;
    this.vitalReadings = vitalReadings;
    this.lookbackDays = lookbackDays;
  }

  @Async("vitalIngestionExecutor")
  public void syncUserAsync(UUID userId, String providerSlug) {
    try {
      syncUser(userId, providerSlug);
    } catch (RuntimeException e) {
      log.error("Post-connect sync failed for user {} provider {}", userId, providerSlug, e);
    }
  }

  public int syncUser(UUID userId, String providerSlug) {
    UUID junctionUserId = accounts.requireJunctionUserId(userId);

    LocalDate end = LocalDate.now(ZoneOffset.UTC);
    LocalDate start = end.minusDays(lookbackDays);

    int total =
        deviceConnections.forUser(userId).stream()
            .filter(connection -> connection.status() == ConnectionStatus.CONNECTED)
            .filter(
                connection ->
                    providerSlug == null || connection.providerSlug().equals(providerSlug))
            .mapToInt(connection -> syncConnection(userId, junctionUserId, connection, start, end))
            .sum();
    String scope = providerSlug == null ? "all providers" : providerSlug;
    log.info("Manual sync of {} for user {} ingested {} new readings", scope, userId, total);
    return total;
  }

  private int syncConnection(
      UUID userId, UUID junctionUserId, DeviceConnection connection, LocalDate start, LocalDate end) {
    Optional<UUID> connectionId = deviceConnections.connectionId(userId, connection.providerSlug());
    if (connectionId.isEmpty()) {
      return 0;
    }
    try {
      List<VitalReading> readings =
          vitalsApi.fetchAll(junctionUserId, connection.providerSlug(), start, end);
      return vitalReadings.saveAll(connectionId.get(), readings);
    } catch (JunctionApiException e) {
      log.warn("Sync fetch failed for {}", connection.providerSlug(), e);
      return 0;
    }
  }
}
