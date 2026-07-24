package com.youth.wearables.externaldevices.infrastructure.webhooks.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HistoricalDataEvent(Data data) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Data(
      @JsonProperty("user_id") UUID userId,
      @JsonProperty("provider") String provider,
      @JsonProperty("start_date") OffsetDateTime startDate,
      @JsonProperty("end_date") OffsetDateTime endDate) {}
}
