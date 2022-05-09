package com.downvoteit.springsolaceconsumerone.receiver;

import com.downvoteit.springcommon.dto.CorKeyDto;
import com.downvoteit.springgpb.ItemReqProto;
import com.downvoteit.springsolacecommon.exception.CheckedPersistenceException;
import com.downvoteit.springsolacecommon.handler.ProducerHandler;
import com.downvoteit.springsolacecommon.listener.ConsumerListener;
import com.downvoteit.springsolacecommon.properties.AppProperties;
import com.downvoteit.springsolaceconsumerone.service.ItemService;
import com.google.protobuf.InvalidProtocolBufferException;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
@Profile("default")
public class PersistentPubSubReceiver {
  private final JCSMPSession session;
  private final EndpointProperties endpointProperties;
  private final ConsumerFlowProperties flowProperties;
  private final Queue queue;
  private final ItemService itemService;

  private FlowReceiver receiver;
  private XMLMessageProducer producer;

  public PersistentPubSubReceiver(
      JCSMPSession session,
      EndpointProperties endpointProperties,
      @Qualifier(AppProperties.CreateItemOltp.FLOW_PROPS) ConsumerFlowProperties flowProperties,
      @Qualifier(AppProperties.CreateItemOlap.QUEUE_UNDO) Queue queue,
      ItemService itemService) {
    this.session = session;
    this.endpointProperties = endpointProperties;
    this.flowProperties = flowProperties;
    this.queue = queue;
    this.itemService = itemService;
  }

  @PostConstruct
  void start() throws JCSMPException {
    producer = session.getMessageProducer(new ProducerHandler() {});

    receiver =
        session.createFlow(
            new ConsumerListener() {
              @Override
              protected void parseMessage(BytesMessage msg) {
                log.info(
                    "Consuming createItem, {}, {}", msg.getDeliveryMode(), msg.getDestination());

                ItemReqProto protoReq = null;
                try {
                  protoReq = ItemReqProto.parseFrom(msg.getData());
                } catch (InvalidProtocolBufferException e) {
                  log.error("", e);
                }

                if (protoReq == null)
                  throw new IllegalStateException("Cannot process message data");

                try {
                  itemService.saveItem(protoReq);
                } catch (CheckedPersistenceException e) {
                  log.warn("Consumed a PersistenceException: {}", e.getMessage());

                  final var protoReqUndo = protoReq;
                  var thread =
                      new Thread(
                          () -> {
                            var msgReq =
                                JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
                            msgReq.setDeliveryMode(DeliveryMode.PERSISTENT);

                            var corKeyDto = CorKeyDto.builder().id(protoReqUndo.getId()).build();
                            msgReq.setCorrelationKey(corKeyDto);
                            msgReq.setData(protoReqUndo.toByteArray());

                            if (Thread.currentThread().isInterrupted()) {
                              log.warn("Polite interruption not supported");
                            }

                            try {
                              producer.send(msgReq, queue);
                            } catch (JCSMPException e2) {
                              log.error("", e2);
                            }
                          });
                  thread.start();
                }
              }
            },
            flowProperties,
            endpointProperties);

    receiver.start();
  }

  @PreDestroy
  void close() {
    if (producer != null && !producer.isClosed()) producer.close();
    if (receiver != null && !receiver.isClosed()) receiver.close();
  }
}
