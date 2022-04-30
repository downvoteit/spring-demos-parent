package com.downvoteit.springsolaceproducer.service;

import com.downvoteit.springgpb.ItemRequest;
import com.downvoteit.springsolacecommon.dto.ItemRequestDto;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProducerService {
  private final XMLMessageProducer producer;
  private final Queue queue;
  private final Topic topic;

  public ProducerService(
      XMLMessageProducer producer, Queue queue, @Qualifier("ledger") Topic topic) {
    this.producer = producer;
    this.queue = queue;
    this.topic = topic;
  }

  public Integer sendQueue(ItemRequestDto itemRequestDto) throws JCSMPException {
    BytesMessage message = sendBytes(itemRequestDto);

    producer.send(message, queue);

    return itemRequestDto.getId();
  }

  public Integer sendTopic(ItemRequestDto itemRequestDto) throws JCSMPException {
    BytesMessage message = sendBytes(itemRequestDto);

    producer.send(message, topic);

    return itemRequestDto.getId();
  }

  private BytesMessage sendBytes(ItemRequestDto itemRequestDto) {
    var message = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
    message.setDeliveryMode(DeliveryMode.PERSISTENT);

    var data =
        ItemRequest.newBuilder()
            .setId(itemRequestDto.getId())
            .setCategoryId(itemRequestDto.getCategoryId())
            .setName(itemRequestDto.getName())
            .setAmount(itemRequestDto.getAmount())
            .setPrice(itemRequestDto.getPrice())
            .build();

    message.setCorrelationKey(data.getId());
    message.setData(data.toByteArray());

    log.info("Produced: \n{}", data);

    return message;
  }
}
