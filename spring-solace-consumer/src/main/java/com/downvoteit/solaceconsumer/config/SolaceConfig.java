package com.downvoteit.solaceconsumer.config;

import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SolaceConfig extends ConsumerListener {
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
  public FlowReceiver createFlowReceiver(JCSMPSession session, Queue queue) throws JCSMPException {
    var flowProperties = new ConsumerFlowProperties();
    flowProperties.setEndpoint(queue);
    flowProperties.setAckMode(JCSMPProperties.SUPPORTED_MESSAGE_ACK_CLIENT);

    var endpointProperties = new EndpointProperties();
    endpointProperties.setAccessType(EndpointProperties.ACCESSTYPE_EXCLUSIVE);

    var receiver = session.createFlow(listener, flowProperties, endpointProperties);

    receiver.start();

    return receiver;
  }
}
