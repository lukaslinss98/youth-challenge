package com.youth.wearables.externaldevices.infrastructure.webhooks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
record DailyDataEvent(Data data) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  record Data(
      @JsonProperty("user_id") UUID userId, Provider provider, List<Point> data) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  record Provider(String slug) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  record Point(
      OffsetDateTime timestamp,
      Double value,
      Double systolic,
      Double diastolic,
      String unit) {}
}
