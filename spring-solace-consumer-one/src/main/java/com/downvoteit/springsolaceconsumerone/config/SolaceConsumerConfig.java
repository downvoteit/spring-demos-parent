package com.downvoteit.springsolaceconsumerone.config;

import com.downvoteit.springgpb.ItemRequest;
import com.downvoteit.springsolacecommon.handler.ProducerHandler;
import com.downvoteit.springsolacecommon.listener.ConsumerListener;
import com.downvoteit.springsolaceconsumerone.entity.Category;
import com.downvoteit.springsolaceconsumerone.entity.Item;
import com.downvoteit.springsolaceconsumerone.service.ProducerService;
import com.google.protobuf.InvalidProtocolBufferException;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

@Slf4j
@Configuration
public class SolaceConsumerConfig {
  private final EntityManagerFactory factory;
  private final ProducerService service;

  public SolaceConsumerConfig(EntityManagerFactory factory, ProducerService service) {
    this.factory = factory;
    this.service = service;
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
              saveItem(data);

              log.info("Consumed: \n{}", data);
            } catch (PersistenceException e) {
              log.warn("Consumed an error: {}", e.getMessage());

              service.rollbackMessage(data, producerSecondaryRollback);
            }
          }
        };

    var receiver = session.createFlow(listener, flowProperties, endpointProperties);

    receiver.start();

    return receiver;
  }

  private void saveItem(ItemRequest data) {
    var manager = factory.createEntityManager();
    var transaction = manager.getTransaction();

    try {
      transaction.begin();

      var category = manager.find(Category.class, data.getCategoryId());

      var item =
          Item.builder()
              .category(category)
              .name(data.getName())
              .amount(data.getAmount())
              .price(data.getPrice())
              .build();

      manager.persist(item);
    } finally {
      transaction.commit();
      manager.close();
    }
  }
}
