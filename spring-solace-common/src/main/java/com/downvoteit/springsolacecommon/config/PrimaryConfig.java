package com.downvoteit.springsolacecommon.config;

import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PrimaryConfig {
  @Value("${queues.main:items-primary}")
  private String queueNamePrimary;

  @Bean("queue-primary")
  public Queue createQueuePrimary(JCSMPSession session, EndpointProperties properties)
      throws JCSMPException {
    var queue = JCSMPFactory.onlyInstance().createQueue(queueNamePrimary);

    session.provision(queue, properties, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);

    return queue;
  }

  @Bean("flow-properties-primary")
  public ConsumerFlowProperties createFlowPropertiesPrimary(
      @Qualifier("queue-primary") Queue queue) {
    return CommonConfig.createGenericFlowProperties(queue);
  }
}
