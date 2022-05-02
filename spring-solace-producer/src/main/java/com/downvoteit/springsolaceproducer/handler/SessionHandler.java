package com.downvoteit.springsolaceproducer.handler;

import com.solacesystems.jcsmp.JCSMPSession;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class SessionHandler {
  private final JCSMPSession session;

  public SessionHandler(JCSMPSession session) {
    this.session = session;
  }

  @PreDestroy
  public void closeSession() {
    if (session != null && !session.isClosed()) session.closeSession();
  }
}
