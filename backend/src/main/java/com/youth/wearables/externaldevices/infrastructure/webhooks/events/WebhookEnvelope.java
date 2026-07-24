package com.youth.wearables.externaldevices.infrastructure.webhooks.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookEnvelope(@JsonProperty("event_type") String eventType) {}
