package com.youth.wearables.integration;

import static com.youth.wearables.integration.support.SvixSignatureTestHelper.currentTimestamp;
import static com.youth.wearables.integration.support.SvixSignatureTestHelper.sign;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.youth.wearables.externaldevices.domain.VitalMetric;
import com.youth.wearables.externaldevices.domain.VitalReading;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

class JunctionWebhookIntegrationTest extends AbstractIntegrationTest {

  private static final String WEBHOOK_PATH = "/api/v1/webhooks/junction";

  @Autowired private ObjectMapper objectMapper;

  @Test
  void webhook_invalidSignature_returns401() throws Exception {
    byte[] payload = connectionEventPayload("provider.connection.created", UUID.randomUUID(), "oura");
    String svixId = "msg_" + UUID.randomUUID();
    String timestamp = currentTimestamp();

    mockMvc
        .perform(
            post(WEBHOOK_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header("svix-id", svixId)
                .header("svix-timestamp", timestamp)
                .header("svix-signature", "v1,not-a-valid-signature")
                .content(payload))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void webhook_missingHeaders_returns400() throws Exception {
    byte[] payload = connectionEventPayload("provider.connection.created", UUID.randomUUID(), "oura");

    mockMvc
        .perform(post(WEBHOOK_PATH).contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  void webhook_connectionCreated_synchronouslyUpdatesDeviceConnection() throws Exception {
    UUID userId = seedUser();
    UUID junctionUserId = UUID.randomUUID();
    jdbcTemplate.update(
        "insert into junction_account (user_id, junction_user_id) values (?, ?)",
        userId,
        junctionUserId);

    performSignedWebhook(connectionEventPayload("provider.connection.created", junctionUserId, "oura"))
        .andExpect(status().isOk());

    String connectionStatus =
        jdbcTemplate.queryForObject(
            "select status from device_connection where user_id = ? and provider_slug = 'oura'",
            String.class,
            userId);
    assertThat(connectionStatus).isEqualTo("CONNECTED");
  }

  @Test
  void webhook_connectionDeleted_synchronouslyUpdatesStatus() throws Exception {
    UUID junctionUserId = UUID.randomUUID();
    UUID userId = seedProvisionedUserWithJunctionId(junctionUserId);
    seedDeviceConnection(userId, "oura", "CONNECTED");

    performSignedWebhook(connectionEventPayload("provider.connection.deleted", junctionUserId, "oura"))
        .andExpect(status().isOk());

    String connectionStatus =
        jdbcTemplate.queryForObject(
            "select status from device_connection where user_id = ? and provider_slug = 'oura'",
            String.class,
            userId);
    assertThat(connectionStatus).isEqualTo("DISCONNECTED");
  }

  @Test
  void webhook_connectionEvent_unknownJunctionUser_isIgnoredGracefully() throws Exception {
    performSignedWebhook(
            connectionEventPayload("provider.connection.created", UUID.randomUUID(), "oura"))
        .andExpect(status().isOk());

    Integer count =
        jdbcTemplate.queryForObject("select count(*) from device_connection", Integer.class);
    assertThat(count).isZero();
  }

  @Test
  void webhook_historicalDataEvent_asyncIngestsVitalReadings() throws Exception {
    UUID userId = seedUser();
    UUID junctionUserId = UUID.randomUUID();
    jdbcTemplate.update(
        "insert into junction_account (user_id, junction_user_id) values (?, ?)",
        userId,
        junctionUserId);
    seedDeviceConnection(userId, "oura", "CONNECTED");
    when(wearableVitalsApi.fetchAll(eq(junctionUserId), eq("oura"), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(
            List.of(new VitalReading(VitalMetric.HEART_RATE, OffsetDateTime.now(), 70.0, "bpm")));

    byte[] payload = historicalDataEventPayload(junctionUserId, "oura");
    performSignedWebhook(payload).andExpect(status().isOk());

    await()
        .atMost(Duration.ofSeconds(5))
        .pollInterval(Duration.ofMillis(100))
        .untilAsserted(
            () -> {
              Integer count =
                  jdbcTemplate.queryForObject("select count(*) from vital_reading", Integer.class);
              assertThat(count).isEqualTo(1);
            });
  }

  @Test
  void webhook_dailyDataEvent_asyncIngestsInlineReadings() throws Exception {
    UUID userId = seedUser();
    UUID junctionUserId = UUID.randomUUID();
    jdbcTemplate.update(
        "insert into junction_account (user_id, junction_user_id) values (?, ?)",
        userId,
        junctionUserId);
    seedDeviceConnection(userId, "oura", "CONNECTED");

    byte[] payload = dailyDataEventPayload(junctionUserId, "oura");
    performSignedWebhook(payload).andExpect(status().isOk());

    await()
        .atMost(Duration.ofSeconds(5))
        .pollInterval(Duration.ofMillis(100))
        .untilAsserted(
            () -> {
              Integer count =
                  jdbcTemplate.queryForObject("select count(*) from vital_reading", Integer.class);
              assertThat(count).isEqualTo(1);
            });
  }

  private ResultActions performSignedWebhook(byte[] payload) throws Exception {
    String svixId = "msg_" + UUID.randomUUID();
    String timestamp = currentTimestamp();
    String signature = sign(svixId, timestamp, payload);

    return mockMvc.perform(
        post(WEBHOOK_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .header("svix-id", svixId)
            .header("svix-timestamp", timestamp)
            .header("svix-signature", signature)
            .content(payload));
  }

  private byte[] connectionEventPayload(String eventType, UUID junctionUserId, String providerSlug) {
    Map<String, Object> body =
        Map.of(
            "event_type",
            eventType,
            "data",
            Map.of("user_id", junctionUserId.toString(), "provider", Map.of("slug", providerSlug)));
    return objectMapper.writeValueAsBytes(body);
  }

  private byte[] historicalDataEventPayload(UUID junctionUserId, String providerSlug) {
    OffsetDateTime end = OffsetDateTime.now();
    OffsetDateTime start = end.minusDays(30);
    Map<String, Object> body =
        Map.of(
            "event_type",
            "historical.data.heartrate.created",
            "data",
            Map.of(
                "user_id", junctionUserId.toString(),
                "provider", providerSlug,
                "start_date", start.toString(),
                "end_date", end.toString()));
    return objectMapper.writeValueAsBytes(body);
  }

  private byte[] dailyDataEventPayload(UUID junctionUserId, String providerSlug) {
    Map<String, Object> point =
        Map.of("timestamp", OffsetDateTime.now().toString(), "value", 70.0, "unit", "bpm");
    Map<String, Object> body =
        Map.of(
            "event_type",
            "daily.data.heartrate.updated",
            "data",
            Map.of(
                "user_id", junctionUserId.toString(),
                "provider", Map.of("slug", providerSlug),
                "data", List.of(point)));
    return objectMapper.writeValueAsBytes(body);
  }

  private UUID seedProvisionedUserWithJunctionId(UUID junctionUserId) {
    UUID userId = seedUser();
    jdbcTemplate.update(
        "insert into junction_account (user_id, junction_user_id) values (?, ?)",
        userId,
        junctionUserId);
    return userId;
  }
}
