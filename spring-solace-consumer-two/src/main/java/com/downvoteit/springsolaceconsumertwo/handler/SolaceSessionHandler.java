package com.downvoteit.springsolaceconsumertwo.handler;

import com.solacesystems.jcsmp.FlowReceiver;
import com.solacesystems.jcsmp.JCSMPSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class SolaceSessionHandler {
  private final JCSMPSession session;
  private final FlowReceiver receiver;
  private final FlowReceiver receiverRollback;

  public SolaceSessionHandler(
      JCSMPSession session,
      @Qualifier("createFlowReceiver") FlowReceiver receiver,
      @Qualifier("createFlowReceiverRollback") FlowReceiver receiverRollback) {
    this.session = session;
    this.receiver = receiver;
    this.receiverRollback = receiverRollback;
  }

  @PreDestroy
  public void closeSession() {
    if (receiver != null && !receiver.isClosed()) receiver.close();
    if (receiverRollback != null && !receiverRollback.isClosed()) receiverRollback.close();
    if (session != null && !session.isClosed()) session.closeSession();
  }
}
