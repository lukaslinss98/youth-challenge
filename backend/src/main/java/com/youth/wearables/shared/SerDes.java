package com.youth.wearables.shared;

import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class SerDes {

  private final ObjectMapper objectMapper;

  SerDes(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public String serialize(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JacksonException e) {
      throw new SerDesException("Failed to serialize " + value.getClass().getName(), e);
    }
  }

  public <T> T deserialize(byte[] data, Class<T> type) {
    try {
      return objectMapper.readValue(data, type);
    } catch (JacksonException e) {
      throw new SerDesException("Failed to deserialize " + type.getName(), e);
    }
  }

  public <T> T deserialize(String data, Class<T> type) {
    try {
      return objectMapper.readValue(data, type);
    } catch (JacksonException e) {
      throw new SerDesException("Failed to deserialize " + type.getName(), e);
    }
  }
}
