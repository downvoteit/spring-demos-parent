package com.downvoteit.springsolaceproducer.service;

import com.downvoteit.springgpb.ItemRequest;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Slf4j
@Component
public class GuaranteedProducerService {
  private final String queueName;
  private final JCSMPSession session;

  @SuppressWarnings({"deprecation"})
  private final JCSMPStreamingPublishEventHandler oldHandler =
      new JCSMPStreamingPublishEventHandler() {
        @Override
        public void responseReceived(String messageID) {
          log.info("Producer received response for message: {}", messageID);
        }

        @Override
        public void handleError(String messageID, JCSMPException e, long timestamp) {
          log.info("Producer received error for message: {}@{} - {}", messageID, timestamp, e);
        }
      };

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

  public GuaranteedProducerService(String queueName, JCSMPSession session) {
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

  @Scheduled(fixedDelay = 5000)
  public void send() throws JCSMPException, NoSuchAlgorithmException {
    var producer = session.getMessageProducer(newHandler);
    var queue = JCSMPFactory.onlyInstance().createQueue(queueName);

    var random = SecureRandom.getInstanceStrong();

    if (random.nextBoolean()) {
      var bytesMessage = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
      bytesMessage.setDeliveryMode(DeliveryMode.PERSISTENT);

      // Use of GPB
      var data =
          ItemRequest.newBuilder()
              .setId(random.nextInt(10))
              .setCategoryId(random.nextInt(100))
              .setName("New item")
              .setAmount(random.nextInt(10000))
              .setPrice(random.nextInt(10) * 10_000 * random.nextDouble())
              .build();
      bytesMessage.setData(data.toByteArray());

      log.info("Producer sent: \n{}", data);

      producer.send(bytesMessage, queue);
    } else {
      var textMessage = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
      textMessage.setDeliveryMode(DeliveryMode.PERSISTENT);

      // Use of text
      var data = String.valueOf(random.nextInt(10));
      textMessage.setText(data);

      log.info("Producer sent: {}", data);

      producer.send(textMessage, queue);
    }
  }
}
