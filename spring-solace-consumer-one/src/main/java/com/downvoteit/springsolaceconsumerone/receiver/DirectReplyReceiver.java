package com.downvoteit.springsolaceconsumerone.receiver;

import com.downvoteit.springcommon.dto.ItemFilterDto;
import com.downvoteit.springgpb.ItemReqNameProto;
import com.downvoteit.springgpb.ItemReqsPageProto;
import com.downvoteit.springsolacecommon.handler.ProducerHandler;
import com.downvoteit.springsolacecommon.listener.ConsumerListener;
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
public class DirectReplyReceiver {
  private final @Qualifier("session-backup") JCSMPSession sessionBackup;
  private final ItemService itemService;

  private XMLMessageProducer producer;
  private XMLMessageConsumer consumer;

  public DirectReplyReceiver(JCSMPSession session, ItemService itemService) {
    this.sessionBackup = session;
    this.itemService = itemService;
  }

  @PostConstruct
  void start() throws JCSMPException {
    producer = sessionBackup.getMessageProducer(new ProducerHandler());

    consumer =
        sessionBackup.getMessageConsumer(
            new ConsumerListener() {
              @Override
              protected void parseMessage(BytesMessage msgReq) {
                if (msgReq.getReplyTo() == null) {
                  log.warn("Reply error: unsupported");

                  return;
                }

                var topicMode = msgReq.getDestination().getName().equals("items/row");

                log.info(
                    "Consuming {}, {}, {}",
                    topicMode ? "getItem" : "getItems",
                    msgReq.getDeliveryMode(),
                    msgReq.getDestination());

                try {
                  if (topicMode) {
                    var protoReq = ItemReqNameProto.parseFrom(msgReq.getData());
                    var protoRes =
                        itemService.getItem(
                            ItemFilterDto.builder()
                                .categoryId(protoReq.getCategoryId())
                                .name(protoReq.getName())
                                .build());

                    var msgRes = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
                    msgRes.setDeliveryMode(DeliveryMode.DIRECT);
                    msgRes.setData(protoRes.toByteArray());

                    producer.sendReply(msgReq, msgRes);
                  } else {
                    var protoReq = ItemReqsPageProto.parseFrom(msgReq.getData());
                    var protoRes = itemService.getItems(protoReq.getPage(), protoReq.getLimit());

                    var msgRes = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
                    msgRes.setDeliveryMode(DeliveryMode.DIRECT);
                    msgRes.setData(protoRes.toByteArray());

                    producer.sendReply(msgReq, msgRes);
                  }
                } catch (JCSMPException | InvalidProtocolBufferException | NoResultException e) {
                  log.error("", e);
                }
              }
            });

    var topic = JCSMPFactory.onlyInstance().createTopic("items/>");
    sessionBackup.addSubscription(topic);

    consumer.start();
  }

  @PreDestroy
  void close() {
    if (producer != null && !producer.isClosed()) producer.close();
    if (consumer != null && !consumer.isClosed()) consumer.stop();
  }
}
