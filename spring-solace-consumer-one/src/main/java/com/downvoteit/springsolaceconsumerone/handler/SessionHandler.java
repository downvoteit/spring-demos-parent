package com.downvoteit.springsolaceconsumerone.handler;

import com.solacesystems.jcsmp.FlowReceiver;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.XMLMessageConsumer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
@Profile("default")
public class SessionHandler {
  private final JCSMPSession session;
  private final FlowReceiver receiver;
  private final XMLMessageConsumer consumer;

  public SessionHandler(JCSMPSession session, FlowReceiver receiver, XMLMessageConsumer consumer) {
    this.session = session;
    this.receiver = receiver;
    this.consumer = consumer;
  }

  @PreDestroy
  public void closeSession() {
    if (consumer != null && !consumer.isClosed()) consumer.close();
    if (receiver != null && !receiver.isClosed()) receiver.close();
    if (session != null && !session.isClosed()) session.closeSession();
  }
}
