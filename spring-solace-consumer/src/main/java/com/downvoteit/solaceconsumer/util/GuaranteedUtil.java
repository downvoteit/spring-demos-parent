package com.downvoteit.solaceconsumer.util;

import com.solacesystems.jcsmp.EndpointProperties;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPSession;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class GuaranteedUtil {
  private final String queueName;
  private final JCSMPSession session;

  public GuaranteedUtil(String queueName, JCSMPSession session) {
    this.queueName = queueName;
    this.session = session;
  }

  @PostConstruct
  public void createQueue() throws JCSMPException {
    final var queue = JCSMPFactory.onlyInstance().createQueue(queueName);

    final var endpointProps = new EndpointProperties();
    endpointProps.setPermission(EndpointProperties.PERMISSION_CONSUME);
    endpointProps.setAccessType(EndpointProperties.ACCESSTYPE_EXCLUSIVE);

    session.provision(queue, endpointProps, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);
  }
}
