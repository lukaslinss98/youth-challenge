package com.youth.wearables.externaldevices.infrastructure.persistence;

import com.youth.wearables.externaldevices.application.ports.JunctionAccounts;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
class JunctionAccountRepositoryImpl implements JunctionAccounts {

  private final JunctionAccountJpaRepository jpaRepository;

  JunctionAccountRepositoryImpl(JunctionAccountJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public boolean exists(UUID userId) {
    return jpaRepository.existsById(userId);
  }

  @Override
  public Optional<UUID> junctionUserId(UUID userId) {
    return jpaRepository.findById(userId).map(JunctionAccountV1::getJunctionUserId);
  }

  @Override
  public Optional<UUID> userIdByJunctionUserId(UUID junctionUserId) {
    return jpaRepository.findByJunctionUserId(junctionUserId).map(JunctionAccountV1::getUserId);
  }

  @Override
  public void link(UUID userId, UUID junctionUserId) {
    jpaRepository.save(new JunctionAccountV1(userId, junctionUserId, LocalDateTime.now()));
  }
}
