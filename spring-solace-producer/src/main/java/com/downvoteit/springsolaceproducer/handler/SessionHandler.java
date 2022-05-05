package com.downvoteit.springsolaceproducer.handler;

import com.downvoteit.springsolacecommon.util.SessionUtil;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
public class SessionHandler {
  private final JCSMPSession session;

  public SessionHandler(JCSMPSession session) {
    this.session = session;
  }

  @PostConstruct
  public void connectSession() throws JCSMPException {
    session.connect();

    SessionUtil.checkCapabilities(session);
  }

  @PreDestroy
  public void closeSession() {
    if (session != null && !session.isClosed()) session.closeSession();
  }
}
