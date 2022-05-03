package com.downvoteit.springsolaceconsumerone.config;

import com.downvoteit.springgpb.ItemNameRequest;
import com.downvoteit.springsolacecommon.handler.ProducerHandler;
import com.downvoteit.springsolacecommon.listener.ConsumerListener;
import com.downvoteit.springsolaceconsumerone.service.ItemService;
import com.google.protobuf.InvalidProtocolBufferException;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.persistence.NoResultException;

@Slf4j
@Configuration
@Profile("default")
public class ReplyConfig {
  private final JCSMPSession session;
  private final ItemService service;

  @Value("${topics.reply:get/item}")
  private String topicGetItem;

  public ReplyConfig(JCSMPSession session, ItemService service) {
    this.session = session;
    this.service = service;
  }

  @Bean
  public XMLMessageConsumer createGetItemConsumer() throws JCSMPException {
    var producer = session.getMessageProducer(new ProducerHandler());
    var consumer =
        session.getMessageConsumer(
            new ConsumerListener() {
              @Override
              public void onReceive(BytesXMLMessage message) {
                if (message.getReplyTo() == null) {
                  log.warn("Reply error: unsupported");

                  return;
                }

                if (message instanceof BytesMessage) {
                  var bytes = ((BytesMessage) message).getData();

                  try {
                    var data = ItemNameRequest.parseFrom(bytes);
                    var name = data.getName();

                    log.info("Reply data preparation for: {}", name);

                    var item = service.getItem(name);

                    var reply = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
                    reply.setData(item.toByteArray());

                    producer.sendReply(message, reply);

                    log.info("Reply data: \n{}", item);
                  } catch (NoResultException e1) {
                    log.error("Reply NoResultException: {}", e1.getMessage());
                  } catch (JCSMPException | InvalidProtocolBufferException e2) {
                    log.error("", e2);
                  }
                }
              }
            });

    var topic = JCSMPFactory.onlyInstance().createTopic(topicGetItem);

    session.addSubscription(topic);
    consumer.start();

    return consumer;
  }
}
