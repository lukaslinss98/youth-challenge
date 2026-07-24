package com.youth.wearables.externaldevices.infrastructure.webhooks.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ConnectionEvent(Data data) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Data(@JsonProperty("user_id") UUID userId, Provider provider) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Provider(String slug) {}
}
