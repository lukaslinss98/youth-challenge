package com.youth.wearables.externaldevices.infrastructure.webhooks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
record WebhookEnvelope(@JsonProperty("event_type") String eventType) {}
