package com.downvoteit.springsolacecommon.config;

import com.downvoteit.springsolacecommon.properties.AppProperties;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PersistentPubSubCreateItemOltpConfig {
  @Bean(AppProperties.CreateItemOltp.QUEUE)
  public Queue createQueue(JCSMPSession session, EndpointProperties properties)
      throws JCSMPException {
    var queue = JCSMPFactory.onlyInstance().createQueue(AppProperties.CreateItemOltp.QUEUE);

    session.provision(queue, properties, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);

    return queue;
  }

  @Bean(AppProperties.CreateItemOltp.FLOW_PROPS)
  public ConsumerFlowProperties createFlowProps(
      @Qualifier(AppProperties.CreateItemOltp.QUEUE) Queue queue) {
    return CommonConfig.createFlowProps(queue);
  }
}
