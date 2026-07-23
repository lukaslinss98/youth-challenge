package com.youth.wearables.externaldevices.infrastructure.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface JunctionAccountJpaRepository extends JpaRepository<JunctionAccountV1, UUID> {}
