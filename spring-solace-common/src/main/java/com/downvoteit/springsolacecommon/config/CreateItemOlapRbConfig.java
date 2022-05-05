package com.downvoteit.springsolacecommon.config;

import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CreateItemOlapRbConfig {
  @Bean(SharedProps.CreateItemOlap.Rollback.QUEUE)
  public Queue createQueue(JCSMPSession session, EndpointProperties properties)
      throws JCSMPException {
    var queue = JCSMPFactory.onlyInstance().createQueue(SharedProps.CreateItemOlap.Rollback.QUEUE);

    session.provision(queue, properties, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);

    return queue;
  }

  @Bean(SharedProps.CreateItemOlap.Rollback.FLOW_PROPS)
  public ConsumerFlowProperties createFlowProps(
      @Qualifier(SharedProps.CreateItemOlap.Rollback.QUEUE) Queue queue) {
    return CommonConfig.createFlowProps(queue);
  }
}
