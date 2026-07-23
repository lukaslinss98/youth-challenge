package com.youth.wearables.junction;

import com.junction.api.Junction;
import com.junction.api.core.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class JunctionClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(JunctionClient.class);

  @Bean
  Junction junction(@Value("${junction.api.key}") String apiKey) {
    LOGGER.info("Using Junction API key %s".formatted(apiKey));
    return Junction.builder().apiKey(apiKey).environment(Environment.SANDBOX_EU).build();
  }
}
