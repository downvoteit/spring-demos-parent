package com.downvoteit.springsolaceproducer.service;

import com.downvoteit.springgpb.ItemRequest;
import com.downvoteit.springsolacecommon.handler.ProducerHandler;
import com.solacesystems.jcsmp.*;
import com.downvoteit.springcommon.dto.ItemCorKeyDto;
import com.downvoteit.springcommon.dto.ItemRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProducerService {
  private final JCSMPSession session;
  private final Queue queuePrimary;
  private final Queue queueSecondary;

  public ProducerService(
      JCSMPSession session,
      @Qualifier("queue-primary") Queue queuePrimary,
      @Qualifier("queue-secondary") Queue queueSecondary) {
    this.session = session;
    this.queuePrimary = queuePrimary;
    this.queueSecondary = queueSecondary;
  }

  public Integer createItem(ItemRequestDto itemRequestDto) throws JCSMPException {
    BytesMessage messagePrimary = prepareCreateItemMessage(itemRequestDto);
    BytesMessage messageSecondary = prepareCreateItemMessage(itemRequestDto);

    var producerPrimary = session.getMessageProducer(new ProducerHandler());
    var producerSecondary = session.getMessageProducer(new ProducerHandler());
    try {
      producerPrimary.send(messagePrimary, queuePrimary);
      producerSecondary.send(messageSecondary, queueSecondary);
    } finally {
      producerPrimary.close();
      producerSecondary.close();
    }

    return itemRequestDto.getId();
  }

  private BytesMessage prepareCreateItemMessage(ItemRequestDto itemRequestDto) {
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
