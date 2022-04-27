package com.downvoteit.springdemos.solacedemo.guaranteed;

import com.solacesystems.jcsmp.InvalidPropertiesException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

/** The type Guaranteed config. */
@Slf4j
@Configuration
@EnableScheduling
@Profile("solace")
public class GuaranteedConfig {
  private final JCSMPProperties properties;

  /**
   * Instantiates a new Guaranteed config.
   *
   * @param properties the properties
   */
  public GuaranteedConfig(JCSMPProperties properties) {
    this.properties = properties;
  }

  /**
   * Queue name string.
   *
   * @return the string
   */
  @Bean
  public String queueName() {
    return "guaranteed";
  }

  /**
   * Create session jcsmp session.
   *
   * @return the jcsmp session
   * @throws InvalidPropertiesException the invalid properties exception
   */
  @Bean
  public JCSMPSession createSession() throws InvalidPropertiesException {
    return JCSMPFactory.onlyInstance()
        .createSession(properties, null, event -> log.debug("Received a Session event: {}", event));
  }
}
