package com.downvoteit.springsolacecommon.config;

import com.solacesystems.jcsmp.ConsumerFlowProperties;
import com.solacesystems.jcsmp.EndpointProperties;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {
  protected static ConsumerFlowProperties createFlowProps(Queue queue) {
    var properties = new ConsumerFlowProperties();
    properties.setEndpoint(queue);
    properties.setAckMode(JCSMPProperties.SUPPORTED_MESSAGE_ACK_CLIENT);

    return properties;
  }

  @Bean
  public EndpointProperties createEndpointProps() {
    var properties = new EndpointProperties();
    properties.setPermission(EndpointProperties.PERMISSION_DELETE);
    properties.setAccessType(EndpointProperties.ACCESSTYPE_EXCLUSIVE);
    properties.setQuota(10);

    return properties;
  }
}
