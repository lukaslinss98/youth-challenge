package com.youth.wearables.externaldevices.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "device_connection")
public class DeviceConnectionV1 {

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "provider_slug", nullable = false)
  private String providerSlug;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  protected DeviceConnectionV1() {}

  public DeviceConnectionV1(
      UUID id,
      UUID userId,
      String providerSlug,
      String status,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.userId = userId;
    this.providerSlug = providerSlug;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public String getProviderSlug() {
    return providerSlug;
  }

  public String getStatus() {
    return status;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
