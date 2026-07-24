package com.youth.wearables.externaldevices.infrastructure.webhooks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
record ConnectionCreatedEvent(Data data) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  record Data(@JsonProperty("user_id") UUID userId, Provider provider) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  record Provider(String slug) {}
}
