package com.downvoteit.springsolaceproducer.service;

import com.downvoteit.springcommon.dto.CorKeyDto;
import com.downvoteit.springcommon.dto.ItemReqDto;
import com.downvoteit.springcommon.dto.ResDto;
import com.downvoteit.springproto.CategoryProto;
import com.downvoteit.springproto.ItemReqProto;
import com.downvoteit.springsolacecommon.handler.ProducerHandler;
import com.downvoteit.springsolacecommon.properties.AppProperties;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PersistentPubSubService {
  private final JCSMPSession session;
  private final Queue createItemOltpQueue;
  private final Queue createItemOlapQueue;

  public PersistentPubSubService(
      JCSMPSession session,
      @Qualifier(AppProperties.CreateItemOltp.QUEUE) Queue createItemOltpQueue,
      @Qualifier(AppProperties.CreateItemOlap.QUEUE) Queue createItemOlapQueue) {
    this.session = session;
    this.createItemOltpQueue = createItemOltpQueue;
    this.createItemOlapQueue = createItemOlapQueue;
  }

  public ResDto createItem(ItemReqDto dto) throws JCSMPException {
    BytesMessage msgOltp = createItemReq(dto);
    BytesMessage msgOlap = createItemReq(dto);

    XMLMessageProducer producer = null;
    try {
      producer = session.getMessageProducer(new ProducerHandler());

      var entries =
          new JCSMPSendMultipleEntry[] {
            JCSMPFactory.onlyInstance().createSendMultipleEntry(msgOltp, createItemOltpQueue),
            JCSMPFactory.onlyInstance().createSendMultipleEntry(msgOlap, createItemOlapQueue)
          };

      producer.sendMultiple(entries, 0, entries.length, 0);
    } finally {
      if (producer != null && !producer.isClosed()) producer.close();
    }

    return ResDto.builder().id(dto.getId()).message("Creation successful").build();
  }

  public BytesMessage createItemReq(ItemReqDto dto) {
    var msgReq = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
    msgReq.setDeliveryMode(DeliveryMode.PERSISTENT);

    var protoReq =
        ItemReqProto.newBuilder()
            .setId(dto.getId())
            .setCategoryId(CategoryProto.forNumber(dto.getCategoryId()))
            .setName(dto.getName())
            .setAmount(dto.getAmount())
            .setPrice(dto.getPrice())
            .build();

    var corKeyDto = CorKeyDto.builder().id(protoReq.getId()).build();
    msgReq.setCorrelationKey(corKeyDto);
    msgReq.setData(protoReq.toByteArray());

    return msgReq;
  }
}
