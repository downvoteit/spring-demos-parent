package com.downvoteit.springsolaceconsumertwo.handler;

import com.downvoteit.springsolacecommon.config.SharedProps;
import com.downvoteit.springsolacecommon.util.SessionUtil;
import com.solacesystems.jcsmp.FlowReceiver;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
public class SessionHandler {
  private final JCSMPSession session;
  private final FlowReceiver receiver;
  private final FlowReceiver receiverRollback;

  public SessionHandler(
      JCSMPSession session,
      @Qualifier(SharedProps.CreateItemOlap.Commit.RECEIVER) FlowReceiver receiver,
      @Qualifier(SharedProps.CreateItemOlap.Rollback.RECEIVER) FlowReceiver receiverRollback) {
    this.session = session;
    this.receiver = receiver;
    this.receiverRollback = receiverRollback;
  }

  @PostConstruct
  public void connectSession() throws JCSMPException {
    session.connect();

    SessionUtil.checkCapabilities(session);
  }

  @PreDestroy
  public void closeSession() {
    if (receiver != null && !receiver.isClosed()) receiver.close();
    if (receiverRollback != null && !receiverRollback.isClosed()) receiverRollback.close();
    if (session != null && !session.isClosed()) session.closeSession();
  }
}
