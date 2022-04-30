package com.downvoteit.springsolaceproducer.config;

import com.downvoteit.springsolacecommon.handlers.ProducerHandler;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SolaceConfig extends ProducerHandler {
  private final SpringJCSMPFactory factory;

  @Value("${queue.name:guaranteed}")
  private String queueName;

  public SolaceConfig(SpringJCSMPFactory factory) {
    super();

    this.factory = factory;
  }

  @Bean
  public JCSMPSession createSession() throws JCSMPException {
    var session = factory.createSession();

    session.connect();

    return session;
  }

  @Bean
  public Queue createQueue(JCSMPSession session) throws JCSMPException {
    final var queue = JCSMPFactory.onlyInstance().createQueue(queueName);

    final var properties = new EndpointProperties();
    properties.setPermission(EndpointProperties.PERMISSION_CONSUME);
    properties.setAccessType(EndpointProperties.ACCESSTYPE_EXCLUSIVE);

    session.provision(queue, properties, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);

    return queue;
  }

  @Bean
  public XMLMessageProducer createProducer(JCSMPSession session) throws JCSMPException {
    return session.getMessageProducer(handler);
  }
}
