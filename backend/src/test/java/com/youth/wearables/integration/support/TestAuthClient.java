package com.youth.wearables.integration.support;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.youth.wearables.usermanagement.infrastructure.controllers.dto.AuthResponseDto;
import com.youth.wearables.usermanagement.infrastructure.controllers.dto.RegisterRequestDto;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

public class TestAuthClient {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;

  public TestAuthClient(MockMvc mockMvc, ObjectMapper objectMapper) {
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
  }

  public record RegisteredUser(UUID userId, String email, String username, String token) {}

  public RegisteredUser registerNewUser() throws Exception {
    String unique = UUID.randomUUID().toString();
    return registerNewUser("user-" + unique + "@example.com", "user" + unique.substring(0, 8));
  }

  public RegisteredUser registerNewUser(String email, String username) throws Exception {
    RegisterRequestDto request = new RegisterRequestDto(username, email, "password123");

    MvcResult result =
        mockMvc
            .perform(
                post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn();

    AuthResponseDto dto =
        objectMapper.readValue(result.getResponse().getContentAsString(), AuthResponseDto.class);
    return new RegisteredUser(
        UUID.fromString(dto.user().id()), dto.user().email(), dto.user().username(), dto.token());
  }

  public HttpHeaders authHeader(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    return headers;
  }
}
