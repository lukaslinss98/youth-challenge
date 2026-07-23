package com.youth.wearables.junction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "junction_account")
class JunctionAccountV1 {

  @Id
  @Column(name = "user_id")
  private UUID userId;

  @Column(name = "junction_user_id", nullable = false, unique = true)
  private UUID junctionUserId;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  protected JunctionAccountV1() {}

  JunctionAccountV1(UUID userId, UUID junctionUserId, LocalDateTime createdAt) {
    this.userId = userId;
    this.junctionUserId = junctionUserId;
    this.createdAt = createdAt;
  }

  UUID getUserId() {
    return userId;
  }

  UUID getJunctionUserId() {
    return junctionUserId;
  }

  LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
