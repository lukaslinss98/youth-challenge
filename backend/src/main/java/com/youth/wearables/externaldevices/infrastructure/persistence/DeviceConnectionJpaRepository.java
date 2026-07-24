package com.youth.wearables.externaldevices.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

interface DeviceConnectionJpaRepository extends JpaRepository<DeviceConnectionV1, UUID> {

  Optional<DeviceConnectionV1> findByUserIdAndProviderSlug(UUID userId, String providerSlug);

  List<DeviceConnectionV1> findByUserId(UUID userId);

  @Modifying
  @Transactional
  @Query(
      value =
          "INSERT INTO device_connection"
              + " (id, user_id, provider_slug, status, created_at, updated_at)"
              + " VALUES (:id, :userId, :providerSlug, :status, :now, :now)"
              + " ON CONFLICT (user_id, provider_slug)"
              + " DO UPDATE SET status = EXCLUDED.status, updated_at = EXCLUDED.updated_at",
      nativeQuery = true)
  void upsert(UUID id, UUID userId, String providerSlug, String status, LocalDateTime now);

  @Modifying
  @Transactional
  @Query(
      value =
          "INSERT INTO device_connection"
              + " (id, user_id, provider_slug, status, created_at, updated_at)"
              + " VALUES (:id, :userId, :providerSlug, 'PENDING', :now, :now)"
              + " ON CONFLICT (user_id, provider_slug)"
              + " DO UPDATE SET status = EXCLUDED.status, updated_at = EXCLUDED.updated_at"
              + " WHERE device_connection.status <> 'CONNECTED'",
      nativeQuery = true)
  void markPending(UUID id, UUID userId, String providerSlug, LocalDateTime now);

  @Modifying
  @Transactional
  @Query(
      "UPDATE DeviceConnectionV1 c SET c.status = :expiredStatus, c.updatedAt = :now"
          + " WHERE c.status = 'PENDING' AND c.updatedAt < :olderThan")
  int expirePending(String expiredStatus, LocalDateTime olderThan, LocalDateTime now);
}
