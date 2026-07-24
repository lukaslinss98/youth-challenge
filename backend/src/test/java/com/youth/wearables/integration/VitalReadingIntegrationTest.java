package com.youth.wearables.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.youth.wearables.externaldevices.domain.VitalMetric;
import com.youth.wearables.externaldevices.domain.VitalReading;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VitalReadingIntegrationTest extends AbstractIntegrationTest {

  @Test
  void getVitals_returnsLatestPerMetric() throws Exception {
    UUID userId = seedProvisionedUser();
    UUID connectionId = seedDeviceConnection(userId, "oura", "CONNECTED");
    seedVitalReading(connectionId, "HEART_RATE", OffsetDateTime.now(), 62.0, "bpm");
    String token = testJwtIssuer.issueToken(userId);

    mockMvc
        .perform(get("/api/v1/vitals").headers(testAuthClient.authHeader(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].provider").value("oura"))
        .andExpect(jsonPath("$[0].metric").value("HEART_RATE"))
        .andExpect(jsonPath("$[0].value").value(62.0))
        .andExpect(jsonPath("$[0].unit").value("bpm"));
  }

  @Test
  void syncVitals_notProvisioned_returns409() throws Exception {
    UUID userId = seedUser();
    String token = testJwtIssuer.issueToken(userId);

    mockMvc
        .perform(post("/api/v1/vitals/sync").headers(testAuthClient.authHeader(token)))
        .andExpect(status().isConflict());
  }

  @Test
  void syncVitals_success_persistsNewReadings() throws Exception {
    UUID userId = seedProvisionedUser();
    seedDeviceConnection(userId, "oura", "CONNECTED");
    String token = testJwtIssuer.issueToken(userId);
    when(wearableVitalsApi.fetchAll(any(UUID.class), eq("oura"), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(
            List.of(new VitalReading(VitalMetric.HEART_RATE, OffsetDateTime.now(), 65.0, "bpm")));

    mockMvc
        .perform(
            post("/api/v1/vitals/sync")
                .param("provider", "oura")
                .headers(testAuthClient.authHeader(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.ingested").value(1));

    Integer count =
        jdbcTemplate.queryForObject("select count(*) from vital_reading", Integer.class);
    assertThat(count).isEqualTo(1);
  }

  @Test
  void syncVitals_dedup_onRepeatedSync_insertsNoDuplicates() throws Exception {
    UUID userId = seedProvisionedUser();
    seedDeviceConnection(userId, "oura", "CONNECTED");
    String token = testJwtIssuer.issueToken(userId);
    OffsetDateTime measuredAt = OffsetDateTime.now();
    when(wearableVitalsApi.fetchAll(any(UUID.class), eq("oura"), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(List.of(new VitalReading(VitalMetric.HEART_RATE, measuredAt, 65.0, "bpm")));

    mockMvc
        .perform(
            post("/api/v1/vitals/sync")
                .param("provider", "oura")
                .headers(testAuthClient.authHeader(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.ingested").value(1));

    mockMvc
        .perform(
            post("/api/v1/vitals/sync")
                .param("provider", "oura")
                .headers(testAuthClient.authHeader(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.ingested").value(0));

    Integer count =
        jdbcTemplate.queryForObject("select count(*) from vital_reading", Integer.class);
    assertThat(count).isEqualTo(1);
  }

  private void seedVitalReading(
      UUID connectionId, String metric, OffsetDateTime measuredAt, double value, String unit) {
    jdbcTemplate.update(
        "insert into vital_reading (id, device_connection_id, metric, measured_at, value, unit) values (?, ?, ?, ?, ?, ?)",
        UUID.randomUUID(),
        connectionId,
        metric,
        measuredAt,
        value,
        unit);
  }
}
