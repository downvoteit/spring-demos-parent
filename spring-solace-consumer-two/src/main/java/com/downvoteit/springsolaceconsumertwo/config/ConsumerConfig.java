package com.downvoteit.springsolaceconsumertwo.config;

import com.downvoteit.springgpb.ItemRequest;
import com.downvoteit.springsolacecommon.listener.ConsumerListener;
import com.downvoteit.springsolaceconsumertwo.service.ItemByCategoryService;
import com.google.protobuf.InvalidProtocolBufferException;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ConsumerConfig {
  private final ItemByCategoryService service;

  public ConsumerConfig(ItemByCategoryService service) {
    this.service = service;
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

              service.updateCategory(data, false);

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

              service.updateCategory(data, true);

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
}
