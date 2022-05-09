package com.downvoteit.springsolacecommon.listener;

import com.downvoteit.springgpb.ItemReqProto;
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
      parseMessage((BytesMessage) message);
    } else {
      log.error("Unsupported message type");
    }

    message.ackMessage();
  }

  protected void parseMessage(BytesMessage message) {
    try {
      var data = ItemReqProto.parseFrom(message.getData());

      log.debug("Consumed: \n{}", data);
    } catch (InvalidProtocolBufferException e) {
      log.error("", e);
    }
  }

  @Override
  public void onException(JCSMPException e) {
    log.error("Consumed an exception:", e);
  }
}
