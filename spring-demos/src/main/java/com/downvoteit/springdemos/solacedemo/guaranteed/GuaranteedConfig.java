package com.downvoteit.springdemos.solacedemo.guaranteed;

import com.solacesystems.jcsmp.InvalidPropertiesException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@Configuration
@EnableScheduling
public class GuaranteedConfig {
  private final JCSMPProperties properties;

  public GuaranteedConfig(JCSMPProperties properties) {
    this.properties = properties;
  }

  @Bean
  public String queueName() {
    return "guaranteed";
  }

  @Bean
  public JCSMPSession producerSession() throws InvalidPropertiesException {
    return JCSMPFactory.onlyInstance()
        .createSession(
            properties, null, event -> log.debug("### Received a Session event: {}", event));
  }

  @Bean
  public JCSMPSession consumerSession() throws InvalidPropertiesException {
    return JCSMPFactory.onlyInstance()
        .createSession(
            properties, null, event -> log.debug("### Received a Session event: {}", event));
  }
}
