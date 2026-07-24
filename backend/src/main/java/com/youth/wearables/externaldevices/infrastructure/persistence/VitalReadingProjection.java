package com.youth.wearables.externaldevices.infrastructure.persistence;

import java.time.Instant;

interface VitalReadingProjection {

  String getProvider();

  String getMetric();

  Instant getMeasuredAt();

  double getValue();

  String getUnit();
}
