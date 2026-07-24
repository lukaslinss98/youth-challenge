package com.youth.wearables.externaldevices.infrastructure.persistence;

import com.youth.wearables.externaldevices.application.ports.DeviceConnections;
import com.youth.wearables.externaldevices.domain.ConnectionStatus;
import com.youth.wearables.externaldevices.domain.DeviceConnection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
class DeviceConnectionRepositoryImpl implements DeviceConnections {

  private final DeviceConnectionJpaRepository jpaRepository;

  DeviceConnectionRepositoryImpl(DeviceConnectionJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public void upsert(UUID userId, String providerSlug, ConnectionStatus status) {
    LocalDateTime now = LocalDateTime.now();
    jpaRepository
        .findByUserIdAndProviderSlug(userId, providerSlug)
        .ifPresentOrElse(
            existing -> {
              existing.setStatus(status.name());
              existing.setUpdatedAt(now);
              jpaRepository.save(existing);
            },
            () ->
                jpaRepository.save(
                    new DeviceConnectionV1(
                        UUID.randomUUID(), userId, providerSlug, status.name(), now, now)));
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
