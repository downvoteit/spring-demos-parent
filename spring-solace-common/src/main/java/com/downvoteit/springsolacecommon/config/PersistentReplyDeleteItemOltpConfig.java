package com.downvoteit.springsolacecommon.config;

import com.downvoteit.springsolacecommon.properties.AppProperties;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PersistentReplyDeleteItemOltpConfig {
  @Bean(AppProperties.DeleteItemOltp.QUEUE)
  public Queue createQueue(JCSMPSession session, EndpointProperties properties)
      throws JCSMPException {
    var queue = JCSMPFactory.onlyInstance().createQueue(AppProperties.DeleteItemOltp.QUEUE);

    session.provision(queue, properties, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);

    return queue;
  }

  @Bean(AppProperties.DeleteItemOltp.FLOW_PROPS)
  public ConsumerFlowProperties createFlowProps(
      @Qualifier(AppProperties.DeleteItemOltp.QUEUE) Queue queue) {
    return CommonConfig.createFlowProps(queue);
  }

  @Bean(AppProperties.DeleteItemOltp.TOPIC)
  public Topic createTopic(
      JCSMPSession session, @Qualifier(AppProperties.DeleteItemOltp.QUEUE) Queue queue)
      throws JCSMPException {
    var topic = JCSMPFactory.onlyInstance().createTopic(AppProperties.DeleteItemOltp.TOPIC);

    session.addSubscription(queue, topic, JCSMPSession.WAIT_FOR_CONFIRM);

    return topic;
  }
}
