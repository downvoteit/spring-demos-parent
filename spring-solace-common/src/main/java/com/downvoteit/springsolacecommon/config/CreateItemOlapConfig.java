package com.downvoteit.springsolacecommon.config;

import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CreateItemOlapConfig {
  @Bean(SharedProps.CreateItemOlap.Commit.QUEUE)
  public Queue createQueue(JCSMPSession session, EndpointProperties properties)
      throws JCSMPException {
    var queue = JCSMPFactory.onlyInstance().createQueue(SharedProps.CreateItemOlap.Commit.QUEUE);

    session.provision(queue, properties, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);

    return queue;
  }

  @Bean(SharedProps.CreateItemOlap.Commit.FLOW_PROPS)
  public ConsumerFlowProperties createFlowProps(
      @Qualifier(SharedProps.CreateItemOlap.Commit.QUEUE) Queue queue) {
    return CommonConfig.createFlowProps(queue);
  }
}
