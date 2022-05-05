package com.downvoteit.springsolaceproducer.service;

import com.downvoteit.springcommon.dto.ItemCorKeyDto;
import com.downvoteit.springcommon.dto.ItemReqDto;
import com.downvoteit.springgpb.CategoryProto;
import com.downvoteit.springgpb.ItemReqProto;
import com.downvoteit.springsolacecommon.config.SharedProps;
import com.downvoteit.springsolacecommon.handler.ProducerHandler;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProducerService {
  private final JCSMPSession session;
  private final Queue createItemOltpQueue;
  private final Queue createItemOlapQueue;

  public ProducerService(
      JCSMPSession session,
      @Qualifier(SharedProps.CreateItemOltp.Commit.QUEUE) Queue createItemOltpQueue,
      @Qualifier(SharedProps.CreateItemOlap.Commit.QUEUE) Queue createItemOlapQueue) {
    this.session = session;
    this.createItemOltpQueue = createItemOltpQueue;
    this.createItemOlapQueue = createItemOlapQueue;
  }

  public Integer createItem(ItemReqDto dto) throws JCSMPException {
    BytesMessage messagePrimary = prepareCreateItemMessage(dto);
    BytesMessage messageSecondary = prepareCreateItemMessage(dto);

    var producerPrimary = session.getMessageProducer(new ProducerHandler());
    var producerSecondary = session.getMessageProducer(new ProducerHandler());
    try {
      producerPrimary.send(messagePrimary, createItemOltpQueue);
      producerSecondary.send(messageSecondary, createItemOlapQueue);
    } finally {
      producerPrimary.close();
      producerSecondary.close();
    }

    return dto.getId();
  }

  private BytesMessage prepareCreateItemMessage(ItemReqDto dto) {
    var message = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
    message.setDeliveryMode(DeliveryMode.PERSISTENT);

    var data =
        ItemReqProto.newBuilder()
            .setId(dto.getId())
            .setCategoryId(CategoryProto.forNumber(dto.getCategoryId()))
            .setName(dto.getName())
            .setAmount(dto.getAmount())
            .setPrice(dto.getPrice())
            .build();

    var corKeyDto = ItemCorKeyDto.builder().id(data.getId()).build();

    message.setCorrelationKey(corKeyDto);
    message.setData(data.toByteArray());

    log.info("Produced: \n{}", data);

    return message;
  }
}
