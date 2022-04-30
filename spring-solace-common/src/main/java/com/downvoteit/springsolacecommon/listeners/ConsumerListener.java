package com.downvoteit.springsolacecommon.listeners;

import com.downvoteit.springgpb.ItemRequest;
import com.google.protobuf.InvalidProtocolBufferException;
import com.solacesystems.jcsmp.BytesMessage;
import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.XMLMessageListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsumerListener implements XMLMessageListener {
  @Override
  public void onReceive(BytesXMLMessage message) {
    if (message instanceof BytesMessage) {
      var bytes = ((BytesMessage) message).getData();

      try {
        var data = ItemRequest.parseFrom(bytes);

        log.info("Consumed: \n{}", data);
        log.info("Consumed dump: \n{}", message.dump());
      } catch (InvalidProtocolBufferException e) {
        log.info("", e);
      }
    } else {
      throw new UnsupportedOperationException("Unsupported message type");
    }

    message.ackMessage();
  }

  @Override
  public void onException(JCSMPException e) {
    log.info("Consumed an exception:", e);
  }
}
