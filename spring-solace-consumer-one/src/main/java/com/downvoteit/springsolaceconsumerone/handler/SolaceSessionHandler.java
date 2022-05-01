package com.downvoteit.springsolaceconsumerone.handler;

import com.solacesystems.jcsmp.FlowReceiver;
import com.solacesystems.jcsmp.JCSMPSession;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class SolaceSessionHandler {
  private final JCSMPSession session;
  private final FlowReceiver receiver;

  public SolaceSessionHandler(JCSMPSession session, FlowReceiver receiver) {
    this.session = session;
    this.receiver = receiver;
  }

  @PreDestroy
  public void closeSession() {
    if (receiver != null && !receiver.isClosed()) receiver.close();
    if (session != null && !session.isClosed()) session.closeSession();
  }
}
