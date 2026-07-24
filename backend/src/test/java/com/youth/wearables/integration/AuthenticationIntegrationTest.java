package com.youth.wearables.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.youth.wearables.integration.support.TestAuthClient.RegisteredUser;
import com.youth.wearables.usermanagement.infrastructure.controllers.dto.LoginRequestDto;
import com.youth.wearables.usermanagement.infrastructure.controllers.dto.RegisterRequestDto;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

class AuthenticationIntegrationTest extends AbstractIntegrationTest {

  @Autowired private ObjectMapper objectMapper;

  @Test
  void register_success_returns201AndToken() throws Exception {
    RegisterRequestDto request = new RegisterRequestDto("newuser", "newuser@example.com", "password123");

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.user.email").value("newuser@example.com"))
        .andExpect(jsonPath("$.user.username").value("newuser"))
        .andExpect(jsonPath("$.token").isNotEmpty());
  }

  @Test
  void register_duplicateEmail_returns409() throws Exception {
    RegisteredUser existing = testAuthClient.registerNewUser("duplicate@example.com", "firstuser");

    RegisterRequestDto request = new RegisterRequestDto("seconduser", existing.email(), "password123");

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  void register_invalidPayload_returns400() throws Exception {
    RegisterRequestDto request = new RegisterRequestDto("", "not-an-email", "short");

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void login_success_returns200AndToken() throws Exception {
    RegisteredUser user = testAuthClient.registerNewUser();

    LoginRequestDto request = new LoginRequestDto(user.email(), "password123");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.email").value(user.email()))
        .andExpect(jsonPath("$.token").isNotEmpty());
  }

  @Test
  void login_badCredentials_returns401() throws Exception {
    RegisteredUser user = testAuthClient.registerNewUser();

    LoginRequestDto request = new LoginRequestDto(user.email(), "wrong-password");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void register_triggersAsyncJunctionProvisioning() throws Exception {
    when(junctionApi.createUser(any(UUID.class))).thenReturn(UUID.randomUUID());

    RegisteredUser user = testAuthClient.registerNewUser();

    await()
        .atMost(Duration.ofSeconds(5))
        .pollInterval(Duration.ofMillis(100))
        .untilAsserted(
            () -> {
              Integer count =
                  jdbcTemplate.queryForObject(
                      "select count(*) from junction_account where user_id = ?",
                      Integer.class,
                      user.userId());
              assertThat(count).isEqualTo(1);
            });
  }
}
