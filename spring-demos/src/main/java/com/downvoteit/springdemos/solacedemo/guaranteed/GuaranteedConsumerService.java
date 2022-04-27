package com.downvoteit.springdemos.solacedemo.guaranteed;

import com.downvoteit.javagoogleprotobuf.Request;
import com.google.protobuf.InvalidProtocolBufferException;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/** The type Guaranteed consumer service. */
@Slf4j
@Component
@Profile("solace")
public class GuaranteedConsumerService {
  private final String queueName;
  private final JCSMPSession session;
  private final XMLMessageListener listener =
      new XMLMessageListener() {
        @Override
        public void onReceive(BytesXMLMessage message) {
          if (message instanceof TextMessage) {
            log.info("Consumer TextMessage received: {}", ((TextMessage) message).getText());
          } else if (message instanceof BytesMessage) {
            var bytes = ((BytesMessage) message).getData();

            try {
              var data = Request.parseFrom(bytes);

              log.info("Consumer ByteMessage received: \n{}", data);
            } catch (InvalidProtocolBufferException e) {
              log.info("", e);
            }
          } else {
            throw new UnsupportedOperationException("Other message types are not supported");
          }

          log.info("Consumer Message Dump: \n{}", message.dump());

          message.ackMessage();
        }

        @Override
        public void onException(JCSMPException e) {
          log.info("Consumer received exception:", e);
        }
      };

  /**
   * Instantiates a new Guaranteed consumer service.
   *
   * @param queueName the queue name
   * @param session the session
   */
  public GuaranteedConsumerService(String queueName, JCSMPSession session) {
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

    receive();
  }

  /** Destroy session. */
  @PreDestroy
  public void destroySession() {
    if (!session.isClosed()) session.closeSession();
  }

  /**
   * Receive.
   *
   * @throws JCSMPException the jcsmp exception
   */
  public void receive() throws JCSMPException {
    var queue = JCSMPFactory.onlyInstance().createQueue(queueName);

    var flowProperties = new ConsumerFlowProperties();
    flowProperties.setEndpoint(queue);
    flowProperties.setAckMode(JCSMPProperties.SUPPORTED_MESSAGE_ACK_CLIENT);

    var endpointProperties = new EndpointProperties();
    endpointProperties.setAccessType(EndpointProperties.ACCESSTYPE_EXCLUSIVE);

    FlowReceiver flow = session.createFlow(listener, flowProperties, endpointProperties);
    flow.start();
  }
}
