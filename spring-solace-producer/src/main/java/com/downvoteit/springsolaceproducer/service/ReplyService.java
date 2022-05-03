package com.downvoteit.springsolaceproducer.service;

import com.downvoteit.springgpb.ItemNameRequest;
import com.downvoteit.springgpb.ItemRequest;
import com.downvoteit.springsolacecommon.handler.ProducerHandler;
import com.google.protobuf.InvalidProtocolBufferException;
import com.solacesystems.jcsmp.*;
import com.downvoteit.springcommon.dto.ItemRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReplyService {
  private final JCSMPSession session;

  @Value("${topics.reply:get/item}")
  private String topicGetItem;

  @Value("${topics.timout:1500}")
  private Integer replyTimeout;

  public ReplyService(JCSMPSession session) {
    this.session = session;
  }

  public ItemRequestDto getItem(String name) throws JCSMPException {
    BytesMessage message = prepareGetItemMessage(name);

    @SuppressWarnings("unused")
    XMLMessageProducer producer = null;
    XMLMessageConsumer consumer = null;
    try {
      producer = session.getMessageProducer(new ProducerHandler());
      consumer = session.getMessageConsumer((XMLMessageListener) null);

      consumer.start();

      var topic = JCSMPFactory.onlyInstance().createTopic(topicGetItem);

      var requestor = session.createRequestor();
      var reply = requestor.request(message, replyTimeout, topic);

      log.info("Reply event scheduled for: {}", name);

      var bytes = ((BytesMessage) reply).getData();
      var data = ItemRequest.parseFrom(bytes);

      log.info("Reply event data: \n{}", data);

      return ItemRequestDto.builder()
          .id(data.getId())
          .categoryId(data.getCategoryId())
          .name(data.getName())
          .amount(data.getAmount())
          .price(data.getPrice())
          .build();
    } catch (JCSMPException e) {
      log.warn("Reply event timeout elapsed for: {}", name);

      throw e;
    } catch (InvalidProtocolBufferException e) {
      log.error("", e);
    } finally {
      if (consumer != null && !consumer.isClosed()) consumer.stop();
    }

    return null;
  }

  private BytesMessage prepareGetItemMessage(String name) {
    var message = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
    message.setDeliveryMode(DeliveryMode.DIRECT);

    var data = ItemNameRequest.newBuilder().setName(name).build();

    message.setData(data.toByteArray());

    return message;
  }
}
