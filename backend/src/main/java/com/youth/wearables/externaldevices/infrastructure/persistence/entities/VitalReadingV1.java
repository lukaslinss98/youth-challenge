package com.youth.wearables.externaldevices.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "vital_reading")
public class VitalReadingV1 {

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "device_connection_id", nullable = false)
  private UUID deviceConnectionId;

  @Column(name = "metric", nullable = false)
  private String metric;

  @Column(name = "measured_at", nullable = false)
  private OffsetDateTime measuredAt;

  @Column(name = "value", nullable = false)
  private double value;

  @Column(name = "unit")
  private String unit;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  protected VitalReadingV1() {}

  VitalReadingV1(
      UUID id,
      UUID deviceConnectionId,
      String metric,
      OffsetDateTime measuredAt,
      double value,
      String unit,
      OffsetDateTime createdAt) {
    this.id = id;
    this.deviceConnectionId = deviceConnectionId;
    this.metric = metric;
    this.measuredAt = measuredAt;
    this.value = value;
    this.unit = unit;
    this.createdAt = createdAt;
  }
}
