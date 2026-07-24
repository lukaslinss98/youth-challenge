package com.youth.wearables.externaldevices.infrastructure.webhooks;

import com.youth.wearables.externaldevices.application.DeviceConnectionEventService;
import com.youth.wearables.externaldevices.application.VitalIngestionService;
import com.youth.wearables.externaldevices.domain.VitalMetric;
import com.youth.wearables.externaldevices.domain.VitalReading;
import com.youth.wearables.externaldevices.domain.VitalResource;
import com.youth.wearables.externaldevices.infrastructure.webhooks.events.ConnectionEvent;
import com.youth.wearables.externaldevices.infrastructure.webhooks.events.DailyDataEvent;
import com.youth.wearables.externaldevices.infrastructure.webhooks.events.HistoricalDataEvent;
import com.youth.wearables.externaldevices.infrastructure.webhooks.events.WebhookEnvelope;
import com.youth.wearables.shared.SerDes;
import com.youth.wearables.shared.SerDesException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhooks/junction")
class JunctionWebhookController {

  private static final Logger log = LoggerFactory.getLogger(JunctionWebhookController.class);
  private static final String CONNECTION_CREATED = "provider.connection.created";
  private static final String CONNECTION_DELETED = "provider.connection.deleted";
  private static final String HISTORICAL_PREFIX = "historical.data.";
  private static final String DAILY_PREFIX = "daily.data.";
  private static final Set<String> HISTORICAL_VITAL_RESOURCES =
      Set.of("heartrate", "hrv", "respiratory_rate", "blood_oxygen", "blood_pressure", "sleep");

  private final SvixWebhookVerifier verifier;
  private final SerDes serDes;
  private final DeviceConnectionEventService eventService;
  private final VitalIngestionService vitalIngestionService;

  JunctionWebhookController(
      SvixWebhookVerifier verifier,
      SerDes serDes,
      DeviceConnectionEventService eventService,
      VitalIngestionService vitalIngestionService) {
    this.verifier = verifier;
    this.serDes = serDes;
    this.eventService = eventService;
    this.vitalIngestionService = vitalIngestionService;
  }

  @PostMapping
  ResponseEntity<Void> receive(
      @RequestBody byte[] payload,
      @RequestHeader("svix-id") String svixId,
      @RequestHeader("svix-timestamp") String svixTimestamp,
      @RequestHeader("svix-signature") String svixSignature) {

    if (!verifier.verify(payload, svixId, svixTimestamp, svixSignature)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    parseEnvelope(payload)
        .ifPresent(
            envelope -> {
              log.info("Received Junction webhook event_type={}", envelope.eventType());
              dispatch(envelope.eventType(), payload);
            });

    return ResponseEntity.ok().build();
  }

  private void dispatch(String eventType, byte[] payload) {
    if (CONNECTION_CREATED.equals(eventType)) {
      handleConnectionEvent(payload, CONNECTION_CREATED, eventService::handleConnectionCreated);
    } else if (CONNECTION_DELETED.equals(eventType)) {
      handleConnectionEvent(payload, CONNECTION_DELETED, eventService::handleConnectionDeleted);
    } else if (isHistoricalPrefixed(eventType)) {
      handleHistoricalIfKnownResource(eventType, payload);
    } else if (isDailyPrefixed(eventType)) {
      handleDailyIfKnownResource(eventType, payload);
    }
  }

  private static boolean isHistoricalPrefixed(String eventType) {
    return eventType != null && eventType.startsWith(HISTORICAL_PREFIX);
  }

  private static boolean isDailyPrefixed(String eventType) {
    return eventType != null && eventType.startsWith(DAILY_PREFIX);
  }

  private void handleHistoricalIfKnownResource(String eventType, byte[] payload) {
    String slug = resourceSlug(eventType, HISTORICAL_PREFIX);
    if (slug != null && HISTORICAL_VITAL_RESOURCES.contains(slug)) {
      handleHistorical(eventType, payload);
    }
  }

  private void handleDailyIfKnownResource(String eventType, byte[] payload) {
    resourceFrom(eventType, DAILY_PREFIX)
        .ifPresent(resource -> handleDaily(eventType, resource, payload));
  }

  private Optional<WebhookEnvelope> parseEnvelope(byte[] payload) {
    try {
      return Optional.of(serDes.deserialize(payload, WebhookEnvelope.class));
    } catch (SerDesException e) {
      log.warn("Could not read Junction webhook envelope; raw payload: {}", raw(payload), e);
      return Optional.empty();
    }
  }

  private <T> Optional<T> tryDeserialize(byte[] payload, Class<T> type, String eventType) {
    try {
      return Optional.of(serDes.deserialize(payload, type));
    } catch (SerDesException e) {
      log.warn("Could not map {} payload: {}", eventType, raw(payload), e);
      return Optional.empty();
    }
  }

  private void handleConnectionEvent(
      byte[] payload, String eventType, BiConsumer<UUID, String> handler) {
    tryDeserialize(payload, ConnectionEvent.class, eventType)
        .map(ConnectionEvent::data)
        .filter(
            data ->
                data != null
                    && data.userId() != null
                    && data.provider() != null
                    && data.provider().slug() != null)
        .ifPresentOrElse(
            data -> handler.accept(data.userId(), data.provider().slug()),
            () -> log.warn("Malformed {} event; raw payload: {}", eventType, raw(payload)));
  }

  private void handleHistorical(String eventType, byte[] payload) {
    tryDeserialize(payload, HistoricalDataEvent.class, eventType)
        .map(HistoricalDataEvent::data)
        .filter(data -> data != null && data.userId() != null && data.provider() != null)
        .ifPresentOrElse(
            data -> ingestHistorical(data),
            () ->
                log.warn(
                    "Historical event {} missing user_id/provider; raw payload: {}",
                    eventType,
                    raw(payload)));
  }

  private void ingestHistorical(HistoricalDataEvent.Data data) {
    LocalDate end =
        data.endDate() != null ? data.endDate().toLocalDate() : LocalDate.now(ZoneOffset.UTC);
    LocalDate start = data.startDate() != null ? data.startDate().toLocalDate() : end.minusDays(30);

    vitalIngestionService.ingestHistorical(data.userId(), data.provider(), start, end);
  }

  private void handleDaily(String eventType, VitalResource resource, byte[] payload) {
    tryDeserialize(payload, DailyDataEvent.class, eventType)
        .map(DailyDataEvent::data)
        .filter(
            data -> data != null && data.userId() != null && data.provider() != null && data.data() != null)
        .ifPresentOrElse(
            data -> ingestDaily(resource, data),
            () ->
                log.warn(
                    "Daily event {} missing fields; raw payload: {}", eventType, raw(payload)));
  }

  private void ingestDaily(VitalResource resource, DailyDataEvent.Data data) {
    List<VitalReading> readings = toReadings(resource, data.data());
    if (!readings.isEmpty()) {
      vitalIngestionService.ingestInline(data.userId(), data.provider().slug(), resource, readings);
    }
  }

  private static List<VitalReading> toReadings(
      VitalResource resource, List<DailyDataEvent.Point> points) {

    List<VitalReading> readings = new ArrayList<>();
    for (DailyDataEvent.Point point : points) {
      if (point.timestamp() == null) {
        continue;
      }
      if (resource == VitalResource.BLOOD_PRESSURE) {
        if (point.systolic() != null) {
          readings.add(
              new VitalReading(
                  VitalMetric.BLOOD_PRESSURE_SYSTOLIC,
                  point.timestamp(),
                  point.systolic(),
                  point.unit()));
        }
        if (point.diastolic() != null) {
          readings.add(
              new VitalReading(
                  VitalMetric.BLOOD_PRESSURE_DIASTOLIC,
                  point.timestamp(),
                  point.diastolic(),
                  point.unit()));
        }
      } else if (point.value() != null) {
        readings.add(
            new VitalReading(metricFor(resource), point.timestamp(), point.value(), point.unit()));
      }
    }
    return readings;
  }

  private static VitalMetric metricFor(VitalResource resource) {
    return switch (resource) {
      case HEART_RATE -> VitalMetric.HEART_RATE;
      case HEART_RATE_VARIABILITY -> VitalMetric.HEART_RATE_VARIABILITY;
      case RESPIRATORY_RATE -> VitalMetric.RESPIRATORY_RATE;
      case BLOOD_OXYGEN -> VitalMetric.BLOOD_OXYGEN;
      case BLOOD_PRESSURE -> throw new IllegalArgumentException("Blood pressure has two metrics");
    };
  }

  private static Optional<VitalResource> resourceFrom(String eventType, String prefix) {
    String slug = resourceSlug(eventType, prefix);
    return slug == null ? Optional.empty() : VitalResource.fromSlug(slug);
  }

  private static String resourceSlug(String eventType, String prefix) {
    String rest = eventType.substring(prefix.length());
    int lastDot = rest.lastIndexOf('.');
    return lastDot < 0 ? null : rest.substring(0, lastDot);
  }

  private static String raw(byte[] payload) {
    return new String(payload, StandardCharsets.UTF_8);
  }
}
