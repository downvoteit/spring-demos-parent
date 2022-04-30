package com.downvoteit.springsolacecommon.listener;

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
      } catch (InvalidProtocolBufferException e) {
        log.error("", e);
      }
    } else {
      log.error("Unsupported message type");
    }

    message.ackMessage();
  }

  @Override
  public void onException(JCSMPException e) {
    log.error("Consumed an exception:", e);
  }
}
