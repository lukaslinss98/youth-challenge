package com.youth.wearables.externaldevices.infrastructure.webhooks;

import com.youth.wearables.externaldevices.application.DeviceConnectionEventService;
import com.youth.wearables.externaldevices.application.VitalIngestionService;
import com.youth.wearables.externaldevices.domain.VitalMetric;
import com.youth.wearables.externaldevices.domain.VitalReading;
import com.youth.wearables.externaldevices.domain.VitalResource;
import com.youth.wearables.shared.SerDes;
import com.youth.wearables.shared.SerDesException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
  private static final String HISTORICAL_PREFIX = "historical.data.";
  private static final String DAILY_PREFIX = "daily.data.";

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

    WebhookEnvelope envelope;
    try {
      envelope = serDes.deserialize(payload, WebhookEnvelope.class);
    } catch (SerDesException e) {
      log.warn("Could not read Junction webhook envelope; raw payload: {}", raw(payload), e);
      return ResponseEntity.ok().build();
    }

    String eventType = envelope.eventType();
    log.info("Received Junction webhook event_type={}", eventType);

    if (CONNECTION_CREATED.equals(eventType)) {
      handleConnectionCreated(payload);
    } else if (eventType != null && eventType.startsWith(HISTORICAL_PREFIX)) {
      resourceFrom(eventType, HISTORICAL_PREFIX)
          .ifPresent(resource -> handleHistorical(eventType, resource, payload));
    } else if (eventType != null && eventType.startsWith(DAILY_PREFIX)) {
      resourceFrom(eventType, DAILY_PREFIX)
          .ifPresent(resource -> handleDaily(eventType, resource, payload));
    }

    return ResponseEntity.ok().build();
  }

  private void handleConnectionCreated(byte[] payload) {
    ConnectionCreatedEvent event;
    try {
      event = serDes.deserialize(payload, ConnectionCreatedEvent.class);
    } catch (SerDesException e) {
      log.warn("Could not map {} payload: {}", CONNECTION_CREATED, raw(payload), e);
      return;
    }

    ConnectionCreatedEvent.Data data = event.data();
    if (data == null
        || data.userId() == null
        || data.provider() == null
        || data.provider().slug() == null) {
      log.warn("Malformed {} event; raw payload: {}", CONNECTION_CREATED, raw(payload));
      return;
    }

    eventService.handleConnectionCreated(data.userId(), data.provider().slug());
  }

  private void handleHistorical(String eventType, VitalResource resource, byte[] payload) {
    HistoricalDataEvent event;
    try {
      event = serDes.deserialize(payload, HistoricalDataEvent.class);
    } catch (SerDesException e) {
      log.warn("Could not map historical event {}; raw payload: {}", eventType, raw(payload), e);
      return;
    }

    HistoricalDataEvent.Data data = event.data();
    if (data == null || data.userId() == null || data.provider() == null) {
      log.warn("Historical event {} missing user_id/provider; raw payload: {}", eventType, raw(payload));
      return;
    }

    LocalDate end =
        data.endDate() != null ? data.endDate().toLocalDate() : LocalDate.now(ZoneOffset.UTC);
    LocalDate start = data.startDate() != null ? data.startDate().toLocalDate() : end.minusDays(30);

    vitalIngestionService.ingestHistorical(data.userId(), data.provider(), resource, start, end);
  }

  private void handleDaily(String eventType, VitalResource resource, byte[] payload) {
    DailyDataEvent event;
    try {
      event = serDes.deserialize(payload, DailyDataEvent.class);
    } catch (SerDesException e) {
      log.warn("Could not map daily event {}; raw payload: {}", eventType, raw(payload), e);
      return;
    }

    DailyDataEvent.Data data = event.data();
    if (data == null || data.userId() == null || data.provider() == null || data.data() == null) {
      log.warn("Daily event {} missing fields; raw payload: {}", eventType, raw(payload));
      return;
    }

    List<VitalReading> readings = toReadings(resource, data.data());
    if (readings.isEmpty()) {
      return;
    }
    vitalIngestionService.ingestInline(data.userId(), data.provider().slug(), resource, readings);
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
    String rest = eventType.substring(prefix.length());
    int lastDot = rest.lastIndexOf('.');
    if (lastDot < 0) {
      return Optional.empty();
    }
    return VitalResource.fromSlug(rest.substring(0, lastDot));
  }

  private static String raw(byte[] payload) {
    return new String(payload, StandardCharsets.UTF_8);
  }
}
