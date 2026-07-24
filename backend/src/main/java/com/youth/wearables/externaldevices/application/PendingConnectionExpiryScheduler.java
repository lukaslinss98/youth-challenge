package com.youth.wearables.externaldevices.application;

import com.youth.wearables.externaldevices.application.ports.DeviceConnections;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PendingConnectionExpiryScheduler {

  private static final Logger log = LoggerFactory.getLogger(PendingConnectionExpiryScheduler.class);

  private final DeviceConnections deviceConnections;
  private final Duration maxPendingAge;

  PendingConnectionExpiryScheduler(
      DeviceConnections deviceConnections,
      @Value("${devices.pending.max-age-minutes:15}") long maxPendingAgeMinutes) {
    this.deviceConnections = deviceConnections;
    this.maxPendingAge = Duration.ofMinutes(maxPendingAgeMinutes);
  }

  @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
  void expireStalePendingConnections() {
    int expired = deviceConnections.expirePending(LocalDateTime.now().minus(maxPendingAge));
    if (expired > 0) {
      log.info("Expired {} stale pending device connections", expired);
    }
  }
}
