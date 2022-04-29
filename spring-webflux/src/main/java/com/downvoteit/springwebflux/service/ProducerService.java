package com.downvoteit.springwebflux.service;

import com.downvoteit.springgpb.ItemRequest;
import com.downvoteit.springwebflux.dto.Item;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
public class ProducerService {
  private final String queueName;
  private final JCSMPSession session;

  private final JCSMPStreamingPublishCorrelatingEventHandler newHandler =
      new JCSMPStreamingPublishCorrelatingEventHandler() {
        @Override
        public void responseReceived(String messageID) {
          log.info("Producer received response for message: {}", messageID);
        }

        @Override
        public void handleError(String messageID, JCSMPException e, long timestamp) {
          log.info("Producer received error for message: {}@{} - {}", messageID, timestamp, e);
        }

        @Override
        public void responseReceivedEx(Object o) {
          log.info("Producer received response");
        }

        @Override
        public void handleErrorEx(Object o, JCSMPException e, long timestamp) {
          log.info("Producer received error for message: {} - {}", timestamp, e);
        }
      };

  public ProducerService(String queueName, JCSMPSession session) {
    this.queueName = queueName;
    this.session = session;
  }

  @PostConstruct
  public void connectSession() throws JCSMPException {
    session.connect();
  }

  @PreDestroy
  public void destroySession() {
    if (!session.isClosed()) session.closeSession();
  }

  public Integer send(Item item) throws JCSMPException {
    var producer = session.getMessageProducer(newHandler);
    var queue = JCSMPFactory.onlyInstance().createQueue(queueName);

    var bytesMessage = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
    bytesMessage.setDeliveryMode(DeliveryMode.PERSISTENT);

    var data =
        ItemRequest.newBuilder()
            .setId(item.getId())
            .setCategoryId(item.getCategoryId())
            .setName(item.getName())
            .setAmount(item.getAmount())
            .setPrice(item.getPrice())
            .build();
    bytesMessage.setData(data.toByteArray());

    log.info("Producer sent: \n{}", data);

    producer.send(bytesMessage, queue);

    return item.getId();
  }
}
