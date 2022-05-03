package com.downvoteit.springsolaceconsumerone.config;

import com.downvoteit.springgpb.ItemRequest;
import com.downvoteit.springsolacecommon.handler.ProducerHandler;
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

import javax.persistence.PersistenceException;

@Slf4j
@Configuration
@Profile("default")
public class ConsumerConfig {
  private final ProducerService producerService;
  private final ItemService itemService;

  public ConsumerConfig(ProducerService producerService, ItemService itemService) {
    this.producerService = producerService;
    this.itemService = itemService;
  }

  @Bean
  public FlowReceiver createFlowReceiver(
      JCSMPSession session,
      @Qualifier("flow-properties-primary") ConsumerFlowProperties flowProperties,
      EndpointProperties endpointProperties)
      throws JCSMPException {
    var producerSecondaryRollback = session.getMessageProducer(new ProducerHandler() {});

    var listener =
        new ConsumerListener() {
          @Override
          protected void parseMessage(BytesMessage message) {
            var bytes = message.getData();

            ItemRequest data = null;
            try {
              data = ItemRequest.parseFrom(bytes);
            } catch (InvalidProtocolBufferException e) {
              log.error("", e);
            }

            if (data == null) throw new IllegalStateException("Cannot process message data");

            try {
              itemService.saveItem(data);

              log.info("Consumed: \n{}", data);
            } catch (PersistenceException e) {
              log.warn("Consumed a PersistenceException: {}", e.getMessage());

              producerService.createItemRollbackMessage(data, producerSecondaryRollback);
            }
          }
        };

    var receiver = session.createFlow(listener, flowProperties, endpointProperties);

    receiver.start();

    return receiver;
  }
}
