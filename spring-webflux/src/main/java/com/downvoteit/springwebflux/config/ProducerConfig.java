package com.downvoteit.springwebflux.config;

import com.solacesystems.jcsmp.InvalidPropertiesException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ProducerConfig {
  private final JCSMPProperties properties;

  public ProducerConfig(JCSMPProperties properties) {
    this.properties = properties;
  }

  @Bean
  public String queueName() {
    return "guaranteed";
  }

  @Bean
  public JCSMPSession createSession() throws InvalidPropertiesException {
    return JCSMPFactory.onlyInstance()
        .createSession(properties, null, event -> log.debug("Received a Session event: {}", event));
  }
}
