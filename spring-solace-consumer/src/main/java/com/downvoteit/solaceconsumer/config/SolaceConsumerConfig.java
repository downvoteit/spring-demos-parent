package com.downvoteit.solaceconsumer.config;

import com.downvoteit.springsolacecommon.listener.ConsumerListener;
import com.solacesystems.jcsmp.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolaceConsumerConfig {
  @Bean
  public FlowReceiver createFlowReceiver(
      JCSMPSession session,
      ConsumerFlowProperties flowProperties,
      EndpointProperties endpointProperties)
      throws JCSMPException {
    var receiver = session.createFlow(new ConsumerListener(), flowProperties, endpointProperties);

    receiver.start();

    return receiver;
  }
}
