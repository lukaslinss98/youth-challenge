package com.youth.wearables.junction;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface JunctionAccountRepository extends JpaRepository<JunctionAccountV1, UUID> {}
