package com.downvoteit.springsolacecommon.handlers;

import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPStreamingPublishCorrelatingEventHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProducerHandler implements JCSMPStreamingPublishCorrelatingEventHandler {
  @Override
  public void responseReceived(String messageID) {
    log.info("Produced a messageID: {}", messageID);
  }

  @Override
  public void handleError(String messageID, JCSMPException e, long timestamp) {
    log.info("Produced an error: {}@{} - {}", messageID, timestamp, e);
  }

  @Override
  public void responseReceivedEx(Object o) {
    log.info("Produced a response: {}", o);
  }

  @Override
  public void handleErrorEx(Object o, JCSMPException e, long timestamp) {
    log.error("Produced an error: {}@{} - {}", o, timestamp, e);
  }
}
