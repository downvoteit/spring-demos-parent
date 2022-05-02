package com.downvoteit.springsolaceconsumertwo.config;

import com.downvoteit.springgpb.ItemRequest;
import com.downvoteit.springsolacecommon.listener.ConsumerListener;
import com.downvoteit.springsolaceconsumertwo.entity.ItemsByCategory;
import com.google.protobuf.InvalidProtocolBufferException;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@Configuration
public class ConsumerConfig {
  private final EntityManagerFactory factory;

  public ConsumerConfig(EntityManagerFactory factory) {
    this.factory = factory;
  }

  @Bean
  public FlowReceiver createFlowReceiver(
      JCSMPSession session,
      @Qualifier("flow-properties-secondary") ConsumerFlowProperties flowProperties,
      EndpointProperties endpointProperties)
      throws JCSMPException {
    var listener =
        new ConsumerListener() {
          @Override
          protected void parseMessage(BytesMessage message) {
            var bytes = message.getData();

            try {
              var data = ItemRequest.parseFrom(bytes);

              updateCategory(data, false);

              log.info("Consumed: \n{}", data);
            } catch (InvalidProtocolBufferException e) {
              log.error("", e);
            }
          }
        };

    var receiver = session.createFlow(listener, flowProperties, endpointProperties);

    receiver.start();

    return receiver;
  }

  @Bean
  public FlowReceiver createFlowReceiverRollback(
      JCSMPSession session,
      @Qualifier("flow-properties-secondary-rollback") ConsumerFlowProperties flowProperties,
      EndpointProperties endpointProperties)
      throws JCSMPException {
    var listener =
        new ConsumerListener() {
          @Override
          protected void parseMessage(BytesMessage message) {
            var bytes = message.getData();

            try {
              var data = ItemRequest.parseFrom(bytes);

              updateCategory(data, true);

              log.info("Consumed rollback: \n{}", data);
            } catch (InvalidProtocolBufferException e) {
              log.error("", e);
            }
          }
        };

    var receiver = session.createFlow(listener, flowProperties, endpointProperties);

    receiver.start();

    return receiver;
  }

  private void updateCategory(ItemRequest data, boolean rollback) {
    var manager = factory.createEntityManager();
    var transaction = manager.getTransaction();

    try {
      transaction.begin();

      var itemByCategory = manager.getReference(ItemsByCategory.class, data.getCategoryId());

      var newAmount = 0;
      var newPrice = 0D;
      if (rollback) {
        newAmount = itemByCategory.getAmount() - data.getAmount();
        newPrice = itemByCategory.getPrice() - data.getPrice();
      } else {
        newAmount = itemByCategory.getAmount() + data.getAmount();
        newPrice = itemByCategory.getPrice() + data.getPrice();
      }

      itemByCategory.setAmount(newAmount);
      itemByCategory.setPrice(newPrice);

      manager.merge(itemByCategory);
    } finally {
      transaction.commit();
      manager.close();
    }
  }
}
