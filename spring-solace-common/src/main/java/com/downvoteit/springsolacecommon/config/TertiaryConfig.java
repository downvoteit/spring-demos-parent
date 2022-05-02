package com.downvoteit.springsolacecommon.config;

import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TertiaryConfig {
  @Value("${queues.main:items-tertiary}")
  private String queueNameTertiary;

  @Value("${topics.primary:goal/primary}")
  private String topicNamePrimary;

  @Value("${topics.secondary:goal/secondary}")
  private String topicNameSecondary;

  @Bean("queue-tertiary")
  public Queue createQueueTertiary(JCSMPSession session, EndpointProperties properties)
      throws JCSMPException {
    var queue = JCSMPFactory.onlyInstance().createQueue(queueNameTertiary);

    session.provision(queue, properties, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);

    return queue;
  }

  @Bean("flow-properties-tertiary")
  public ConsumerFlowProperties createFlowPropertiesTertiary(
      @Qualifier("queue-tertiary") Queue queue) {
    return CommonConfig.createGenericFlowProperties(queue);
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
