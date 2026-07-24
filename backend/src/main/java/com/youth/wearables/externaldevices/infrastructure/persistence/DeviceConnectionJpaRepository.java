package com.youth.wearables.externaldevices.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface DeviceConnectionJpaRepository extends JpaRepository<DeviceConnectionV1, UUID> {

  Optional<DeviceConnectionV1> findByUserIdAndProviderSlug(UUID userId, String providerSlug);

  List<DeviceConnectionV1> findByUserId(UUID userId);
}
