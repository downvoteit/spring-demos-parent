package com.downvoteit.springsolaceconsumerone.handler;

import com.downvoteit.springsolacecommon.config.SharedProps;
import com.downvoteit.springsolacecommon.util.SessionUtil;
import com.solacesystems.jcsmp.FlowReceiver;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
@Profile("default")
public class SessionHandler {
  private final JCSMPSession session;
  private final FlowReceiver createItemReceiver;
  private final FlowReceiver getItemReceiver;
  private final FlowReceiver getItemsReceiver;

  public SessionHandler(
      JCSMPSession session,
      @Qualifier(SharedProps.CreateItemOltp.Commit.RECEIVER) FlowReceiver createItemReceiver,
      @Qualifier(SharedProps.GetItemOltp.RECEIVER) FlowReceiver getItemReceiver,
      @Qualifier(SharedProps.GetItemsOltp.RECEIVER) FlowReceiver getItemsReceiver) {
    this.session = session;
    this.createItemReceiver = createItemReceiver;
    this.getItemReceiver = getItemReceiver;
    this.getItemsReceiver = getItemsReceiver;
  }

  @PostConstruct
  public void connectSession() throws JCSMPException {
    session.connect();

    SessionUtil.checkCapabilities(session);
  }

  @PreDestroy
  public void closeSession() {
    if (createItemReceiver != null && !createItemReceiver.isClosed()) createItemReceiver.close();
    if (getItemReceiver != null && !getItemReceiver.isClosed()) getItemReceiver.close();
    if (getItemsReceiver != null && !getItemsReceiver.isClosed()) getItemsReceiver.close();
    if (session != null && !session.isClosed()) session.closeSession();
  }
}
