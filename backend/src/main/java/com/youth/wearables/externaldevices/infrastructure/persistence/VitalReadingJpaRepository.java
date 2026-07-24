package com.youth.wearables.externaldevices.infrastructure.persistence;

import com.youth.wearables.externaldevices.infrastructure.persistence.entities.VitalReadingV1;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

interface VitalReadingJpaRepository extends JpaRepository<VitalReadingV1, UUID> {

  @Modifying
  @Transactional
  @Query(
      value =
          "INSERT INTO vital_reading"
              + " (id, device_connection_id, metric, measured_at, value, unit, created_at)"
              + " SELECT * FROM unnest("
              + " cast(:ids as uuid[]), cast(:connectionIds as uuid[]), cast(:metrics as text[]),"
              + " cast(:measuredAts as timestamptz[]), cast(:values as double precision[]),"
              + " cast(:units as text[]), cast(:createdAts as timestamptz[]))"
              + " ON CONFLICT (device_connection_id, metric, measured_at) DO NOTHING",
      nativeQuery = true)
  int batchInsertIgnore(
      UUID[] ids,
      UUID[] connectionIds,
      String[] metrics,
      OffsetDateTime[] measuredAts,
      Double[] values,
      String[] units,
      OffsetDateTime[] createdAts);

  @Query(
      value =
          "SELECT DISTINCT ON (dc.provider_slug, vr.metric) dc.provider_slug AS provider,"
              + " vr.metric AS metric, vr.measured_at AS measuredAt, vr.value AS value,"
              + " vr.unit AS unit"
              + " FROM vital_reading vr"
              + " JOIN device_connection dc ON dc.id = vr.device_connection_id"
              + " WHERE dc.user_id = :userId"
              + " ORDER BY dc.provider_slug, vr.metric, vr.measured_at DESC",
      nativeQuery = true)
  List<VitalReadingProjection> findLatestPerMetricForUser(UUID userId);
}
