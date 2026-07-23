package com.youth.wearables.usermanagement.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserJpaRepository extends JpaRepository<UserV1, UUID> {

  Optional<UserV1> findByEmail(String email);
}
