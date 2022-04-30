package com.downvoteit.solaceconsumer.config;

import com.downvoteit.springgpb.ItemRequest;
import com.google.protobuf.InvalidProtocolBufferException;
import com.solacesystems.jcsmp.BytesMessage;
import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.XMLMessageListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsumerListener {
  protected final XMLMessageListener listener =
      new XMLMessageListener() {
        @Override
        public void onReceive(BytesXMLMessage message) {
          if (message instanceof BytesMessage) {
            var bytes = ((BytesMessage) message).getData();

            try {
              var data = ItemRequest.parseFrom(bytes);

              log.info("Consumer ByteMessage received: \n{}", data);
            } catch (InvalidProtocolBufferException e) {
              log.info("", e);
            }
          } else {
            throw new UnsupportedOperationException("Other message types are not supported");
          }

          log.info("Consumer Message dump: \n{}", message.dump());

          message.ackMessage();
        }

        @Override
        public void onException(JCSMPException e) {
          log.info("Consumer received exception:", e);
        }
      };
}
