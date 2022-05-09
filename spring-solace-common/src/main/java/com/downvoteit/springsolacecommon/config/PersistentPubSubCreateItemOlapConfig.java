package com.downvoteit.springsolacecommon.config;

import com.downvoteit.springsolacecommon.properties.AppProperties;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PersistentPubSubCreateItemOlapConfig {
  @Bean(AppProperties.CreateItemOlap.QUEUE)
  public Queue createQueue(JCSMPSession session, EndpointProperties properties)
      throws JCSMPException {
    var queue = JCSMPFactory.onlyInstance().createQueue(AppProperties.CreateItemOlap.QUEUE);

    session.provision(queue, properties, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);

    return queue;
  }

  @Bean(AppProperties.CreateItemOlap.FLOW_PROPS)
  public ConsumerFlowProperties createFlowProps(
      @Qualifier(AppProperties.CreateItemOlap.QUEUE) Queue queue) {
    return CommonConfig.createFlowProps(queue);
  }

  @Bean(AppProperties.CreateItemOlap.QUEUE_UNDO)
  public Queue createQueueUndo(JCSMPSession session, EndpointProperties properties)
      throws JCSMPException {
    var queue = JCSMPFactory.onlyInstance().createQueue(AppProperties.CreateItemOlap.QUEUE_UNDO);

    session.provision(queue, properties, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);

    return queue;
  }

  @Bean(AppProperties.CreateItemOlap.FLOW_PROPS_UNDO)
  public ConsumerFlowProperties createFlowPropsUndo(
      @Qualifier(AppProperties.CreateItemOlap.QUEUE_UNDO) Queue queue) {
    return CommonConfig.createFlowProps(queue);
  }
}
