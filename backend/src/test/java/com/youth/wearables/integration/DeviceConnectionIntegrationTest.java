package com.youth.wearables.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.youth.wearables.externaldevices.domain.LinkToken;
import com.youth.wearables.externaldevices.domain.WearableProvider;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DeviceConnectionIntegrationTest extends AbstractIntegrationTest {

  @Test
  void listDevices_requiresAuth_returns401WithoutToken() throws Exception {
    mockMvc.perform(get("/api/v1/devices")).andExpect(status().isUnauthorized());
  }

  @Test
  void linkToken_notProvisioned_returns409() throws Exception {
    UUID userId = seedUser();
    String token = testJwtIssuer.issueToken(userId);

    mockMvc
        .perform(
            post("/api/v1/devices/link-token")
                .param("provider", "oura")
                .headers(testAuthClient.authHeader(token)))
        .andExpect(status().isConflict());
  }

  @Test
  void linkToken_provisioned_returns200WithToken() throws Exception {
    UUID userId = seedProvisionedUser();
    String token = testJwtIssuer.issueToken(userId);
    when(junctionApi.createLinkToken(any(UUID.class), eq(WearableProvider.OURA)))
        .thenReturn(new LinkToken("tok_abc", "https://link.example/abc"));

    mockMvc
        .perform(
            post("/api/v1/devices/link-token")
                .param("provider", "oura")
                .headers(testAuthClient.authHeader(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.linkToken").value("tok_abc"))
        .andExpect(jsonPath("$.linkWebUrl").value("https://link.example/abc"));
  }

  @Test
  void demoConnect_oura_returns200() throws Exception {
    UUID userId = seedProvisionedUser();
    String token = testJwtIssuer.issueToken(userId);
    doNothing().when(junctionApi).connectDemo(any(UUID.class), eq(WearableProvider.OURA));

    mockMvc
        .perform(
            post("/api/v1/devices/demo-connect")
                .param("provider", "oura")
                .headers(testAuthClient.authHeader(token)))
        .andExpect(status().isOk());

    Integer count =
        jdbcTemplate.queryForObject(
            "select count(*) from device_connection where user_id = ? and provider_slug = 'oura' and status = 'CONNECTED'",
            Integer.class,
            userId);
    assertThat(count).isEqualTo(1);
  }

  @Test
  void disconnect_returns204AndUpdatesStatus() throws Exception {
    UUID userId = seedProvisionedUser();
    seedDeviceConnection(userId, "oura", "CONNECTED");
    String token = testJwtIssuer.issueToken(userId);
    doNothing().when(junctionApi).deregisterProvider(any(UUID.class), eq("oura"));

    mockMvc
        .perform(delete("/api/v1/devices/oura").headers(testAuthClient.authHeader(token)))
        .andExpect(status().isNoContent());

    String connectionStatus =
        jdbcTemplate.queryForObject(
            "select status from device_connection where user_id = ? and provider_slug = 'oura'",
            String.class,
            userId);
    assertThat(connectionStatus).isEqualTo("DISCONNECTED");
  }

  @Test
  void listDevices_returnsSeededConnections() throws Exception {
    UUID userId = seedProvisionedUser();
    seedDeviceConnection(userId, "oura", "CONNECTED");
    String token = testJwtIssuer.issueToken(userId);

    mockMvc
        .perform(get("/api/v1/devices").headers(testAuthClient.authHeader(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].provider").value("oura"))
        .andExpect(jsonPath("$[0].status").value("CONNECTED"));
  }
}
