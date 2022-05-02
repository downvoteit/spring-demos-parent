package com.downvoteit.springsolaceconsumerone.config;

import com.downvoteit.springgpb.ItemNameRequest;
import com.downvoteit.springgpb.ItemRequest;
import com.downvoteit.springsolacecommon.handler.ProducerHandler;
import com.downvoteit.springsolacecommon.listener.ConsumerListener;
import com.downvoteit.springsolaceconsumerone.entity.Item;
import com.google.protobuf.InvalidProtocolBufferException;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;

@Slf4j
@Configuration
public class ReplyConfig {
  private final EntityManagerFactory factory;
  private final JCSMPSession session;

  @Value("${topics.reply:get/item}")
  private String topicGetItem;

  public ReplyConfig(EntityManagerFactory factory, JCSMPSession session) {
    this.factory = factory;
    this.session = session;
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

                    var item = getItem(name);

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

  private ItemRequest getItem(String name) {
    var manager = factory.createEntityManager();
    var transaction = manager.getTransaction();

    try {
      transaction.begin();

      var data =
          manager
              .createQuery("select t from items t where t.name = :name", Item.class)
              .setParameter("name", name)
              .getSingleResult();

      return ItemRequest.newBuilder()
          .setCategoryId(data.getCategory().getId())
          .setName(data.getName())
          .setAmount(data.getAmount())
          .setPrice(data.getPrice())
          .build();
    } finally {
      transaction.rollback();
      manager.close();
    }
  }
}
