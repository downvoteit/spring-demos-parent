package com.downvoteit.springsolaceconsumerone.config.receiver;

import com.downvoteit.springgpb.ItemReqProto;
import com.downvoteit.springsolacecommon.config.SharedProps;
import com.downvoteit.springsolacecommon.exception.CheckedPersistenceException;
import com.downvoteit.springsolacecommon.listener.ConsumerListener;
import com.downvoteit.springsolaceconsumerone.service.ItemService;
import com.downvoteit.springsolaceconsumerone.service.ProducerService;
import com.google.protobuf.InvalidProtocolBufferException;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@Profile("default")
public class ConsumerCreateItemReceiverConfig {
  private final ProducerService producerService;
  private final ItemService itemService;

  public ConsumerCreateItemReceiverConfig(
      ProducerService producerService, ItemService itemService) {
    this.producerService = producerService;
    this.itemService = itemService;
  }

  @Bean(SharedProps.CreateItemOltp.Commit.RECEIVER)
  public FlowReceiver createReceiver(
      JCSMPSession session,
      @Qualifier(SharedProps.CreateItemOltp.Commit.FLOW_PROPS)
          ConsumerFlowProperties flowProperties,
      EndpointProperties endpointProperties)
      throws JCSMPException {
    var listener = getConsumerListener();
    var receiver = session.createFlow(listener, flowProperties, endpointProperties);

    receiver.start();

    return receiver;
  }

  private ConsumerListener getConsumerListener() {
    return new ConsumerListener() {
      @Override
      protected void parseMessage(BytesMessage message) {
        var bytes = message.getData();

        ItemReqProto data = null;
        try {
          data = ItemReqProto.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
          log.error("", e);
        }

        if (data == null) throw new IllegalStateException("Cannot process message data");

        try {
          itemService.saveItem(data);

          log.info("Consumed: \n{}", data);
        } catch (CheckedPersistenceException e) {
          log.warn("Consumed a PersistenceException: {}", e.getMessage());

          producerService.createItemRollbackMessage(data);
        }
      }
    };
  }
}
