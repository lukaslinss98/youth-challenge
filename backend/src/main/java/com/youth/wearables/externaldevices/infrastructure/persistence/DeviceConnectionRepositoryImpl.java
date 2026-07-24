package com.youth.wearables.externaldevices.infrastructure.persistence;

import com.youth.wearables.externaldevices.application.ports.DeviceConnections;
import com.youth.wearables.externaldevices.domain.ConnectionStatus;
import com.youth.wearables.externaldevices.domain.DeviceConnection;
import com.youth.wearables.externaldevices.infrastructure.persistence.entities.DeviceConnectionV1;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
class DeviceConnectionRepositoryImpl implements DeviceConnections {

  private static final Logger log = LoggerFactory.getLogger(DeviceConnectionRepositoryImpl.class);

  private final DeviceConnectionJpaRepository jpaRepository;

  DeviceConnectionRepositoryImpl(DeviceConnectionJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public void upsert(UUID userId, String providerSlug, ConnectionStatus status) {
    jpaRepository.upsert(
        UUID.randomUUID(), userId, providerSlug, status.name(), LocalDateTime.now());
  }

  @Override
  public void markPending(UUID userId, String providerSlug) {
    jpaRepository.markPending(UUID.randomUUID(), userId, providerSlug, LocalDateTime.now());
  }

  @Override
  public int expirePending(LocalDateTime olderThan) {
    return jpaRepository.expirePending(
        ConnectionStatus.ERROR.name(), olderThan, LocalDateTime.now());
  }

  @Override
  public void updateStatus(UUID userId, String providerSlug, ConnectionStatus status) {
    jpaRepository
        .findByUserIdAndProviderSlug(userId, providerSlug)
        .ifPresentOrElse(
            existing -> {
              existing.setStatus(status.name());
              existing.setUpdatedAt(LocalDateTime.now());
              jpaRepository.save(existing);
            },
            () ->
                log.warn(
                    "No device connection to update for user {} provider {}", userId, providerSlug));
  }

  @Override
  public Optional<UUID> connectionId(UUID userId, String providerSlug) {
    return jpaRepository
        .findByUserIdAndProviderSlug(userId, providerSlug)
        .map(DeviceConnectionV1::getId);
  }

  @Override
  public List<DeviceConnection> forUser(UUID userId) {
    return jpaRepository.findByUserId(userId).stream()
        .map(
            entity ->
                new DeviceConnection(
                    entity.getProviderSlug(),
                    ConnectionStatus.valueOf(entity.getStatus()),
                    entity.getUpdatedAt()))
        .toList();
  }
}
