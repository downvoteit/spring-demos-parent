package com.downvoteit.springsolaceconsumerone.config.receiver;

import com.downvoteit.springgpb.ItemReqNameProto;
import com.downvoteit.springsolacecommon.config.SharedProps;
import com.downvoteit.springsolacecommon.handler.ProducerHandler;
import com.downvoteit.springsolacecommon.listener.ConsumerListener;
import com.downvoteit.springsolaceconsumerone.service.ItemService;
import com.google.protobuf.InvalidProtocolBufferException;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.persistence.NoResultException;

@Slf4j
@Configuration
@Profile("default")
public class ConsumerGetItemReceiverReplyConfig {
  private final ItemService itemService;

  public ConsumerGetItemReceiverReplyConfig(ItemService itemService) {
    this.itemService = itemService;
  }

  @Bean(SharedProps.GetItemOltp.RECEIVER)
  public FlowReceiver createReceiver(
      JCSMPSession session,
      @Qualifier(SharedProps.GetItemOltp.FLOW_PROPS) ConsumerFlowProperties flowProperties,
      EndpointProperties endpointProperties)
      throws JCSMPException {
    var producer = session.getMessageProducer(new ProducerHandler());
    var listener = getConsumerListener(producer);
    var receiver = session.createFlow(listener, flowProperties, endpointProperties);

    receiver.start();

    return receiver;
  }

  private ConsumerListener getConsumerListener(XMLMessageProducer producer) {
    return new ConsumerListener() {
      @Override
      protected void parseMessage(BytesMessage message) {
        if (message.getReplyTo() == null) {
          log.warn("Reply error: unsupported");

          return;
        }

        var bytes = message.getData();

        try {
          var data = ItemReqNameProto.parseFrom(bytes);
          var name = data.getName();

          log.info("Reply data preparation for: {}", name);
          log.info("Reply destination: {}", message.getReplyTo());

          var replyData = itemService.getItem(name);

          var reply = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
          reply.setData(replyData.toByteArray());

          producer.sendReply(message, reply);

          log.info("Reply data: \n{}", replyData);
        } catch (NoResultException e1) {
          log.error("Reply NoResultException: {}", e1.getMessage());
        } catch (JCSMPException | InvalidProtocolBufferException e2) {
          log.error("", e2);
        }
      }
    };
  }
}
