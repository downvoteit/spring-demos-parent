package com.downvoteit.springsolacecommon.config;

import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GetItemsConfig {
  @Bean(SharedProps.GetItemsOltp.QUEUE)
  public Queue createQueue(JCSMPSession session, EndpointProperties properties)
      throws JCSMPException {
    var queue = JCSMPFactory.onlyInstance().createQueue(SharedProps.GetItemsOltp.QUEUE);

    session.provision(queue, properties, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);

    return queue;
  }

  @Bean(SharedProps.GetItemsOltp.FLOW_PROPS)
  public ConsumerFlowProperties createFlowProps(
      @Qualifier(SharedProps.GetItemsOltp.QUEUE) Queue queue) {
    return CommonConfig.createFlowProps(queue);
  }

  @Bean(SharedProps.GetItemsOltp.TOPIC)
  public Topic createTopic(
      JCSMPSession session, @Qualifier(SharedProps.GetItemsOltp.QUEUE) Queue queue)
      throws JCSMPException {
    var topic = JCSMPFactory.onlyInstance().createTopic(SharedProps.GetItemsOltp.TOPIC);

    session.addSubscription(queue, topic, JCSMPSession.WAIT_FOR_CONFIRM);

    return topic;
  }
}
