package com.downvoteit.springsolaceproducer.service;

import com.downvoteit.springgpb.ItemRequest;
import com.downvoteit.springsolacecommon.dto.ItemRequestDto;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProducerService {
  private final XMLMessageProducer producer;
  private final Queue queue;

  public ProducerService(XMLMessageProducer producer, Queue queue) {
    this.producer = producer;
    this.queue = queue;
  }

  public Integer send(ItemRequestDto itemRequestDto) throws JCSMPException {
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
    message.setData(data.toByteArray());

    log.info("Producer sent: \n{}", data);

    producer.send(message, queue);

    return itemRequestDto.getId();
  }
}
