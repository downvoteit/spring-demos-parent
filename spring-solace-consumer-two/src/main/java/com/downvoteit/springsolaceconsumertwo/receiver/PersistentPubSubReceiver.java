package com.downvoteit.springsolaceconsumertwo.receiver;

import com.downvoteit.springproto.ItemReqProto;
import com.downvoteit.springsolacecommon.listener.ConsumerListener;
import com.downvoteit.springsolacecommon.properties.AppProperties;
import com.downvoteit.springsolaceconsumertwo.service.ItemsCategoryService;
import com.google.protobuf.InvalidProtocolBufferException;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
public class PersistentPubSubReceiver {
  private final JCSMPSession session;
  private final EndpointProperties endpointProperties;
  private final ConsumerFlowProperties flowProperties;
  private final ConsumerFlowProperties flowPropertiesUndo;
  private final ItemsCategoryService itemsCategoryService;
  private FlowReceiver receiver;
  private FlowReceiver receiverUndo;

  public PersistentPubSubReceiver(
      JCSMPSession session,
      EndpointProperties endpointProperties,
      @Qualifier(AppProperties.CreateItemOlap.FLOW_PROPS) ConsumerFlowProperties flowProperties,
      @Qualifier(AppProperties.CreateItemOlap.FLOW_PROPS_UNDO)
          ConsumerFlowProperties flowPropertiesUndo,
      ItemsCategoryService itemsCategoryService) {
    this.session = session;
    this.endpointProperties = endpointProperties;
    this.flowProperties = flowProperties;
    this.flowPropertiesUndo = flowPropertiesUndo;
    this.itemsCategoryService = itemsCategoryService;
  }

  @PostConstruct
  void start() throws JCSMPException {
    receiver =
        session.createFlow(
            new ConsumerListener() {
              @Override
              protected void parseMessage(BytesMessage message) {
                log.info(
                    "Consuming createItem, {}, {}",
                    message.getDeliveryMode(),
                    message.getDestination());

                try {
                  var protoReq = ItemReqProto.parseFrom(message.getData());

                  itemsCategoryService.updateCategory(protoReq, false);
                } catch (InvalidProtocolBufferException e) {
                  log.error("", e);
                }
              }
            },
            flowProperties,
            endpointProperties);

    receiverUndo =
        session.createFlow(
            new ConsumerListener() {
              @Override
              protected void parseMessage(BytesMessage message) {
                log.info(
                    "Consuming createItem (undo), {}, {}",
                    message.getDeliveryMode(),
                    message.getDestination());

                try {
                  var protoReq = ItemReqProto.parseFrom(message.getData());

                  itemsCategoryService.updateCategory(protoReq, true);
                } catch (InvalidProtocolBufferException e) {
                  log.error("", e);
                }
              }
            },
            flowPropertiesUndo,
            endpointProperties);

    receiver.start();
    receiverUndo.start();
  }

  @PreDestroy
  void close() {
    if (receiver != null && !receiver.isClosed()) receiver.close();
    if (receiverUndo != null && !receiverUndo.isClosed()) receiverUndo.close();
  }
}
