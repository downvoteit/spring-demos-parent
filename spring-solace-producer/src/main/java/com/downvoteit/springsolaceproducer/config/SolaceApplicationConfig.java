package com.downvoteit.springsolaceproducer.config;

import com.downvoteit.springsolacecommon.util.SolaceSessionUtil;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SolaceApplicationConfig {
  @Value("${queues.main:items-primary}")
  private String queueNamePrimary;

  @Value("${queues.main:items-secondary}")
  private String queueNameSecondary;

  @Value("${queues.main:items-secondary-rollback}")
  private String queueNameSecondaryRollback;

  @Value("${queues.main:items-tertiary}")
  private String queueNameTertiary;

  @Value("${topics.ledger:goal/primary}")
  private String topicNamePrimary;

  @Value("${topics.analytics:goal/secondary}")
  private String topicNameSecondary;

  @Bean
  public JCSMPSession createSession(JCSMPProperties properties) throws JCSMPException {
    properties.setProperty(JCSMPProperties.IGNORE_DUPLICATE_SUBSCRIPTION_ERROR, true);

    var session = JCSMPFactory.onlyInstance().createSession(properties);

    session.connect();

    SolaceSessionUtil.checkCapabilities(session);

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

  @Bean("queue-primary")
  public Queue createQueuePrimary(JCSMPSession session, EndpointProperties properties)
      throws JCSMPException {
    var queue = JCSMPFactory.onlyInstance().createQueue(queueNamePrimary);

    session.provision(queue, properties, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);

    return queue;
  }

  @Bean("queue-secondary")
  public Queue createQueueSecondary(JCSMPSession session, EndpointProperties properties)
      throws JCSMPException {
    var queue = JCSMPFactory.onlyInstance().createQueue(queueNameSecondary);

    session.provision(queue, properties, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);

    return queue;
  }

  @Bean("queue-secondary-rollback")
  public Queue createQueueSecondaryRollback(JCSMPSession session, EndpointProperties properties)
      throws JCSMPException {
    var queue = JCSMPFactory.onlyInstance().createQueue(queueNameSecondaryRollback);

    session.provision(queue, properties, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);

    return queue;
  }

  @Bean("queue-tertiary")
  public Queue createQueueTertiary(JCSMPSession session, EndpointProperties properties)
      throws JCSMPException {
    var queue = JCSMPFactory.onlyInstance().createQueue(queueNameTertiary);

    session.provision(queue, properties, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);

    return queue;
  }

  @Bean("flow-properties-primary")
  public ConsumerFlowProperties createFlowPropertiesPrimary(
      @Qualifier("queue-primary") Queue queue) {
    return createGenericFlowProperties(queue);
  }

  @Bean("flow-properties-secondary")
  public ConsumerFlowProperties createFlowPropertiesSecondary(
      @Qualifier("queue-secondary") Queue queue) {
    return createGenericFlowProperties(queue);
  }

  @Bean("flow-properties-secondary-rollback")
  public ConsumerFlowProperties createFlowPropertiesSecondaryRollback(
      @Qualifier("queue-secondary-rollback") Queue queue) {
    return createGenericFlowProperties(queue);
  }

  @Bean("flow-properties-tertiary")
  public ConsumerFlowProperties createFlowPropertiesTertiary(
      @Qualifier("queue-tertiary") Queue queue) {
    return createGenericFlowProperties(queue);
  }

  private ConsumerFlowProperties createGenericFlowProperties(Queue queue) {
    var properties = new ConsumerFlowProperties();
    properties.setEndpoint(queue);
    properties.setAckMode(JCSMPProperties.SUPPORTED_MESSAGE_ACK_CLIENT);

    return properties;
  }

  @Bean("topic-primary")
  public Topic createTopicPrimary(JCSMPSession session, @Qualifier("queue-tertiary") Queue queue)
      throws JCSMPException {
    var topic = JCSMPFactory.onlyInstance().createTopic(topicNamePrimary);

    session.addSubscription(queue, topic, JCSMPSession.WAIT_FOR_CONFIRM);

    return topic;
  }

  @Bean("topic-secondary")
  public Topic createTopicSecondary(JCSMPSession session, @Qualifier("queue-tertiary") Queue queue)
      throws JCSMPException {
    var topic = JCSMPFactory.onlyInstance().createTopic(topicNameSecondary);

    session.addSubscription(queue, topic, JCSMPSession.WAIT_FOR_CONFIRM);

    return topic;
  }
}
