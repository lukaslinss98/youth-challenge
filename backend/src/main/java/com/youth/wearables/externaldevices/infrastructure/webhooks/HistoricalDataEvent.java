package com.youth.wearables.externaldevices.infrastructure.webhooks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
record HistoricalDataEvent(Data data) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  record Data(
      @JsonProperty("user_id") UUID userId,
      @JsonProperty("provider") String provider,
      @JsonProperty("start_date") OffsetDateTime startDate,
      @JsonProperty("end_date") OffsetDateTime endDate) {}
}
