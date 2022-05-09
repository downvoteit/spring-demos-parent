package com.downvoteit.springsolaceconsumerone.receiver;

import com.downvoteit.springcommon.dto.CorKeyDto;
import com.downvoteit.springgpb.ReqProto;
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
import javax.persistence.NoResultException;

@Slf4j
@Component
@Profile("default")
public class SemiPersistentRequestReplyReceiver {
  private final JCSMPSession session;
  private final EndpointProperties endpointProperties;
  private final ConsumerFlowProperties flowProperties;
  private final Queue queue;
  private final ItemService itemService;

  private FlowReceiver receiver;
  private XMLMessageProducer producer;

  public SemiPersistentRequestReplyReceiver(
      JCSMPSession session,
      EndpointProperties endpointProperties,
      @Qualifier(AppProperties.DeleteItemOltp.FLOW_PROPS) ConsumerFlowProperties flowProperties,
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
    producer = session.getMessageProducer(new ProducerHandler());

    receiver =
        session.createFlow(
            new ConsumerListener() {
              @Override
              protected void parseMessage(BytesMessage msgReq) {
                if (msgReq.getReplyTo() == null) {
                  log.warn("Reply error: unsupported");

                  return;
                }

                log.info(
                    "Consuming deleteItem, {}, {}",
                    msgReq.getDeliveryMode(),
                    msgReq.getDestination());

                try {
                  var protoReq = ReqProto.parseFrom(msgReq.getData());
                  var protoRes = itemService.deleteItem(protoReq.getId());

                  var msgRes = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
                  msgRes.setDeliveryMode(DeliveryMode.DIRECT);
                  msgRes.setData(protoRes.toByteArray());

                  producer.sendReply(msgReq, msgRes);

                  var thread =
                      new Thread(
                          () -> {
                            var msgReqOlap =
                                JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
                            msgReqOlap.setDeliveryMode(DeliveryMode.PERSISTENT);

                            var corKeyDto = CorKeyDto.builder().id(protoRes.getId()).build();
                            msgReqOlap.setCorrelationKey(corKeyDto);
                            msgReqOlap.setData(protoRes.toByteArray());

                            if (Thread.currentThread().isInterrupted()) {
                              log.warn("Polite interruption not supported");
                            }

                            try {
                              producer.send(msgReqOlap, queue);
                            } catch (JCSMPException e2) {
                              log.error("", e2);
                            }
                          });
                  thread.start();
                } catch (JCSMPException | InvalidProtocolBufferException | NoResultException e) {
                  log.error("", e);
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
