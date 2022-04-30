package com.downvoteit.springsolaceproducer.service;

import com.downvoteit.springgpb.ItemRequest;
import com.solacesystems.jcsmp.*;
import dto.ItemCorKeyDto;
import dto.ItemRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
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

    var corKeyDto = ItemCorKeyDto.builder().id(data.getId()).build();

    message.setCorrelationKey(corKeyDto);
    message.setData(data.toByteArray());

    log.info("Produced: \n{}", data);

    return message;
  }
}
