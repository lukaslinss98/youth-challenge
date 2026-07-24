package com.youth.wearables.externaldevices.domain;

import java.time.OffsetDateTime;

public record VitalReading(
    VitalMetric metric, OffsetDateTime measuredAt, double value, String unit) {}
