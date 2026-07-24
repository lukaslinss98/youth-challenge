package com.youth.wearables.externaldevices.infrastructure.persistence;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface VitalReadingJpaRepository extends JpaRepository<VitalReadingV1, UUID> {

  @Query(
      "select v.measuredAt from VitalReadingV1 v"
          + " where v.deviceConnectionId = ?1 and v.metric = ?2 and v.measuredAt between ?3 and ?4")
  List<OffsetDateTime> findMeasuredAt(
      UUID deviceConnectionId, String metric, OffsetDateTime from, OffsetDateTime to);
}
