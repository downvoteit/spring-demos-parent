package com.downvoteit.springdemos.solacedemo.guaranteed;

import com.downvoteit.javagoogleprotobuf.Request;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/** The type Guaranteed producer service. */
@Slf4j
@Component
@Profile("solace")
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

  /**
   * Instantiates a new Guaranteed producer service.
   *
   * @param queueName the queue name
   * @param session the session
   */
  public GuaranteedProducerService(String queueName, JCSMPSession session) {
    this.queueName = queueName;
    this.session = session;
  }

  /**
   * Connect session.
   *
   * @throws JCSMPException the jcsmp exception
   */
  @PostConstruct
  public void connectSession() throws JCSMPException {
    session.connect();
  }

  /** Destroy session. */
  @PreDestroy
  public void destroySession() {
    if (!session.isClosed()) session.closeSession();
  }

  /**
   * Send.
   *
   * @throws JCSMPException the jcsmp exception
   * @throws NoSuchAlgorithmException the no such algorithm exception
   */
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
          Request.newBuilder().setId(random.nextInt(10)).setAmount(random.nextInt(100)).build();
      bytesMessage.setData(data.toByteArray());

      log.info("Producer sent: \n{}", data.getId());

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
