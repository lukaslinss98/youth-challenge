package com.youth.wearables.externaldevices.infrastructure.persistence;

import com.youth.wearables.externaldevices.infrastructure.persistence.entities.JunctionAccountV1;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface JunctionAccountJpaRepository extends JpaRepository<JunctionAccountV1, UUID> {

  Optional<JunctionAccountV1> findByJunctionUserId(UUID junctionUserId);
}
