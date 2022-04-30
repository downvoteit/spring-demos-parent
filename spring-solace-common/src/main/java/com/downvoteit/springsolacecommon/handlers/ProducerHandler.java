package com.downvoteit.springsolacecommon.handlers;

import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPStreamingPublishCorrelatingEventHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProducerHandler {
  protected final JCSMPStreamingPublishCorrelatingEventHandler handler =
      new JCSMPStreamingPublishCorrelatingEventHandler() {
        @Override
        public void responseReceived(String messageID) {
          log.info("Producer received response for message: {}", messageID);
        }

        @Override
        public void handleError(String messageID, JCSMPException e, long timestamp) {
          log.info("Producer received error for message: {}@{} - {}", messageID, timestamp, e);
        }

        @Override
        public void responseReceivedEx(Object o) {
          log.warn("Producer received response: {}", o);
        }

        @Override
        public void handleErrorEx(Object o, JCSMPException e, long timestamp) {
          log.error("Producer received error for message: {} - {}", timestamp, e);
        }
      };
}
