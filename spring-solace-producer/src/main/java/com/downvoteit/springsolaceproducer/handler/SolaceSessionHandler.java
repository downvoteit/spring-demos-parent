package com.downvoteit.springsolaceproducer.handler;

import com.solacesystems.jcsmp.JCSMPSession;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class SolaceSessionHandler {
  private final JCSMPSession session;

  public SolaceSessionHandler(JCSMPSession session) {
    this.session = session;
  }

  @PreDestroy
  public void closeSession() {
    if (session != null && !session.isClosed()) session.closeSession();
  }
}
