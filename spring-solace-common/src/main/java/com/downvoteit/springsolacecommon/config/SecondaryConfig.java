package com.downvoteit.springsolacecommon.config;

import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SecondaryConfig {
  @Value("${queues.main:items-secondary}")
  private String queueNameSecondary;

  @Value("${queues.main:items-secondary-rollback}")
  private String queueNameSecondaryRollback;

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

  @Bean("flow-properties-secondary")
  public ConsumerFlowProperties createFlowPropertiesSecondary(
      @Qualifier("queue-secondary") Queue queue) {
    return CommonConfig.createGenericFlowProperties(queue);
  }

  @Bean("flow-properties-secondary-rollback")
  public ConsumerFlowProperties createFlowPropertiesSecondaryRollback(
      @Qualifier("queue-secondary-rollback") Queue queue) {
    return CommonConfig.createGenericFlowProperties(queue);
  }
}
