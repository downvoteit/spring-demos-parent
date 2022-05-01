package com.downvoteit.solaceconsumer.config;

import com.downvoteit.solaceconsumer.entity.Category;
import com.downvoteit.solaceconsumer.entity.Item;
import com.downvoteit.springgpb.ItemRequest;
import com.downvoteit.springsolacecommon.listener.ConsumerListener;
import com.google.protobuf.InvalidProtocolBufferException;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@Configuration
public class SolaceConsumerConfig {
  private final EntityManagerFactory factory;

  public SolaceConsumerConfig(EntityManagerFactory factory) {
    this.factory = factory;
  }

  @Bean
  public FlowReceiver createFlowReceiver(
      JCSMPSession session,
      ConsumerFlowProperties flowProperties,
      EndpointProperties endpointProperties)
      throws JCSMPException {

    var listener =
        new ConsumerListener() {
          @Override
          protected void parseMessage(BytesMessage message) {
            var bytes = message.getData();

            try {
              var data = ItemRequest.parseFrom(bytes);

              saveItem(data);

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

  private void saveItem(ItemRequest data) {
    var manager = factory.createEntityManager();
    var transaction = manager.getTransaction();

    try {
      transaction.begin();

      var category = manager.getReference(Category.class, data.getCategoryId());

      var item =
          new Item(data.getId(), category, data.getName(), data.getAmount(), data.getPrice());

      manager.merge(item);
    } finally {
      transaction.commit();
      manager.close();
    }
  }
}
