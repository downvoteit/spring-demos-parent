package com.downvoteit.springsolacecommon.config;

import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SolaceConfig {
  @Value("${queues.main:items}")
  private String queueName;

  @Value("${topics.ledger:goal/ledger}")
  private String ledgerTopicName;

  @Value("${topics.analytics:goal/analytics}")
  private String analyticsTopicName;

  @Bean
  public JCSMPSession createSession(JCSMPProperties properties) throws JCSMPException {
    properties.setProperty(JCSMPProperties.IGNORE_DUPLICATE_SUBSCRIPTION_ERROR, true);

    var session = JCSMPFactory.onlyInstance().createSession(properties);

    session.connect();

    return session;
  }

  @Bean
  public EndpointProperties createEndpointProperties() {
    var properties = new EndpointProperties();
    properties.setPermission(EndpointProperties.PERMISSION_DELETE);
    properties.setAccessType(EndpointProperties.ACCESSTYPE_EXCLUSIVE);
    properties.setQuota(100);

    return properties;
  }

  @Bean
  public ConsumerFlowProperties createFlowProperties(Queue queue) {
    var properties = new ConsumerFlowProperties();
    properties.setEndpoint(queue);
    properties.setAckMode(JCSMPProperties.SUPPORTED_MESSAGE_ACK_CLIENT);

    return properties;
  }

  @Bean
  public Queue createQueue(JCSMPSession session, EndpointProperties properties)
      throws JCSMPException {
    if (session.isCapable(CapabilityType.PUB_GUARANTEED)
        && session.isCapable(CapabilityType.SUB_FLOW_GUARANTEED)
        && session.isCapable(CapabilityType.ENDPOINT_MANAGEMENT)
        && session.isCapable(CapabilityType.QUEUE_SUBSCRIPTIONS)) {
      log.info("All required capabilities supported");
    } else {
      String message =
          "Missing required capability"
              + "Capability - PUB_GUARANTEED: {}\n"
              + "Capability - SUB_FLOW_GUARANTEED: {}\n"
              + "Capability - ENDPOINT_MANAGEMENT: {}\n"
              + "Capability - QUEUE_SUBSCRIPTIONS: {}";
      log.error(
          message,
          session.isCapable(CapabilityType.PUB_GUARANTEED),
          session.isCapable(CapabilityType.SUB_FLOW_GUARANTEED),
          session.isCapable(CapabilityType.ENDPOINT_MANAGEMENT),
          session.isCapable(CapabilityType.QUEUE_SUBSCRIPTIONS));
    }

    var queue = JCSMPFactory.onlyInstance().createQueue(queueName);

    session.provision(queue, properties, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);

    return queue;
  }

  @Bean("ledger")
  public Topic createTopicItem(JCSMPSession session, Queue queue) throws JCSMPException {
    var topic = JCSMPFactory.onlyInstance().createTopic(ledgerTopicName);

    session.addSubscription(queue, topic, JCSMPSession.WAIT_FOR_CONFIRM);

    return topic;
  }

  @Bean("analytics")
  public Topic createTopic(JCSMPSession session, Queue queue) throws JCSMPException {
    var topic = JCSMPFactory.onlyInstance().createTopic(analyticsTopicName);

    session.addSubscription(queue, topic, JCSMPSession.WAIT_FOR_CONFIRM);

    return topic;
  }
}
