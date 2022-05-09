package com.downvoteit.springsolacecommon.handler;

import com.downvoteit.springcommon.dto.CorKeyDto;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPStreamingPublishCorrelatingEventHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProducerHandler implements JCSMPStreamingPublishCorrelatingEventHandler {
  @Override
  public void responseReceived(String messageID) {
    log.debug("Produced a messageID: {}", messageID);
  }

  @Override
  public void handleError(String messageID, JCSMPException e, long timestamp) {
    log.debug("Produced an error: {}@{} - {}", messageID, timestamp, e);
  }

  @Override
  public void responseReceivedEx(Object o) {
    if (o instanceof CorKeyDto) {
      var corKeyDto = (CorKeyDto) o;
      corKeyDto.setAcked(true);
      corKeyDto.setPublished(true);
    }
    log.debug("Produced a response: {}", o);
  }

  @Override
  public void handleErrorEx(Object o, JCSMPException e, long timestamp) {
    if (o instanceof CorKeyDto) {
      var corKeyDto = (CorKeyDto) o;
      corKeyDto.setAcked(true);
    }
    log.error("Produced an error: {}@{} - {}", o, timestamp, e);
  }
}
