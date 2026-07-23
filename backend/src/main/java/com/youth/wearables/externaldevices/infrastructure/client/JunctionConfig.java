package com.youth.wearables.externaldevices.infrastructure.client;

import com.junction.api.Junction;
import com.junction.api.core.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class JunctionConfig {

  @Bean
  Junction junction(@Value("${junction.api.key}") String apiKey) {
    return Junction.builder().apiKey(apiKey).environment(Environment.SANDBOX_EU).build();
  }
}
