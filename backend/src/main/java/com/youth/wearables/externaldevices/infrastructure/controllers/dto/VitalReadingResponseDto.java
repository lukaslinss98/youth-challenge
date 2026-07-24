package com.youth.wearables.externaldevices.infrastructure.controllers.dto;

import java.time.OffsetDateTime;

public record VitalReadingResponseDto(
    String provider, String metric, double value, String unit, OffsetDateTime measuredAt) {}
