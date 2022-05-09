package com.downvoteit.springsolaceconsumertwo.receiver;

import com.downvoteit.springgpb.ItemReqProto;
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
  private final ConsumerFlowProperties flowPropertiesCommit;
  private final ConsumerFlowProperties flowPropertiesRollback;
  private final ItemsCategoryService itemsCategoryService;
  private FlowReceiver receiverCommit;
  private FlowReceiver receiverRollback;

  public PersistentPubSubReceiver(
      JCSMPSession session,
      EndpointProperties endpointProperties,
      @Qualifier(AppProperties.CreateItemOlap.FLOW_PROPS)
          ConsumerFlowProperties flowPropertiesCommit,
      @Qualifier(AppProperties.CreateItemOlap.FLOW_PROPS_UNDO)
          ConsumerFlowProperties flowPropertiesRollback,
      ItemsCategoryService itemsCategoryService) {
    this.session = session;
    this.endpointProperties = endpointProperties;
    this.flowPropertiesCommit = flowPropertiesCommit;
    this.flowPropertiesRollback = flowPropertiesRollback;
    this.itemsCategoryService = itemsCategoryService;
  }

  @PostConstruct
  void start() throws JCSMPException {
    receiverCommit =
        session.createFlow(
            new ConsumerListener() {
              @Override
              protected void parseMessage(BytesMessage message) {
                log.info(
                    "Consuming createItem (commit), {}, {}",
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
            flowPropertiesCommit,
            endpointProperties);

    receiverRollback =
        session.createFlow(
            new ConsumerListener() {
              @Override
              protected void parseMessage(BytesMessage message) {
                log.info(
                    "Consuming createItem (rollback), {}, {}",
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
            flowPropertiesRollback,
            endpointProperties);

    receiverCommit.start();
    receiverRollback.start();
  }

  @PreDestroy
  void close() {
    if (receiverCommit != null && !receiverCommit.isClosed()) receiverCommit.close();
    if (receiverRollback != null && !receiverRollback.isClosed()) receiverRollback.close();
  }
}
