package com.youth.wearables.integration;

import com.youth.wearables.externaldevices.application.ports.JunctionApi;
import com.youth.wearables.externaldevices.application.ports.WearableVitalsApi;
import com.youth.wearables.integration.support.SvixSignatureTestHelper;
import com.youth.wearables.integration.support.TestAuthClient;
import com.youth.wearables.integration.support.TestJwtIssuer;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.postgresql.PostgreSQLContainer;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
abstract class AbstractIntegrationTest {

  @DynamicPropertySource
  static void junctionProperties(DynamicPropertyRegistry registry) {
    registry.add("junction.api.key", () -> "test-api-key");
    registry.add("junction.webhook.secret", () -> SvixSignatureTestHelper.TEST_WEBHOOK_SECRET);
  }

  static final PostgreSQLContainer POSTGRES =
      new PostgreSQLContainer("postgres:17")
          .withDatabaseName("wearables")
          .withUsername("wearables")
          .withPassword("wearables");

  static {
    POSTGRES.start();
  }

  @ServiceConnection
  static PostgreSQLContainer postgresConnection() {
    return POSTGRES;
  }

  @MockitoBean protected JunctionApi junctionApi;
  @MockitoBean protected WearableVitalsApi wearableVitalsApi;

  @Autowired protected MockMvc mockMvc;
  @Autowired protected JdbcTemplate jdbcTemplate;
  @Autowired private ObjectMapper objectMapper;
  @Value("${jwt.secret}") private String jwtSecret;
  @Value("${jwt.expiration-ms}") private long jwtExpirationMs;

  protected TestAuthClient testAuthClient;
  protected TestJwtIssuer testJwtIssuer;

  @BeforeEach
  void initSupportClients() {
    testAuthClient = new TestAuthClient(mockMvc, objectMapper);
    testJwtIssuer = new TestJwtIssuer(jwtSecret, jwtExpirationMs);
  }

  @AfterEach
  void cleanDatabase() {
    jdbcTemplate.execute(
        "TRUNCATE TABLE vital_reading, device_connection, junction_account, app_user CASCADE");
  }

  protected UUID seedUser() {
    UUID userId = UUID.randomUUID();
    jdbcTemplate.update(
        "insert into app_user (id, email, password_hash, username) values (?, ?, ?, ?)",
        userId,
        "seed-" + userId + "@example.com",
        "unused-hash",
        "seed-" + userId.toString().substring(0, 8));
    return userId;
  }

  protected UUID seedProvisionedUser() {
    UUID userId = seedUser();
    jdbcTemplate.update(
        "insert into junction_account (user_id, junction_user_id) values (?, ?)",
        userId,
        UUID.randomUUID());
    return userId;
  }

  protected UUID seedDeviceConnection(UUID userId, String providerSlug, String status) {
    UUID connectionId = UUID.randomUUID();
    jdbcTemplate.update(
        "insert into device_connection (id, user_id, provider_slug, status) values (?, ?, ?, ?)",
        connectionId,
        userId,
        providerSlug,
        status);
    return connectionId;
  }
}
