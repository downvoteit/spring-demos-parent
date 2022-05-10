package com.downvoteit.springsolaceproducer.service;

import com.downvoteit.springcommon.dto.ItemFilterDto;
import com.downvoteit.springcommon.dto.ItemReqDto;
import com.downvoteit.springcommon.dto.ItemReqsDto;
import com.downvoteit.springproto.ItemReqNameProto;
import com.downvoteit.springproto.ItemReqProto;
import com.downvoteit.springproto.ItemReqsPageProto;
import com.downvoteit.springproto.ItemReqsProto;
import com.downvoteit.springsolacecommon.handler.ProducerHandler;
import com.google.protobuf.InvalidProtocolBufferException;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class DirectRequestReplyService {
  private final JCSMPSession session;

  @Value("${topics.timout:500}")
  private Integer replyTimeout;

  public DirectRequestReplyService(JCSMPSession session) {
    this.session = session;
  }

  public ItemReqDto getItem(ItemFilterDto dto) throws JCSMPException {
    var msgReq = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
    msgReq.setDeliveryMode(DeliveryMode.DIRECT);

    var protoReq =
        ItemReqNameProto.newBuilder()
            .setCategoryId(dto.getCategoryId())
            .setName(dto.getName())
            .build();
    msgReq.setData(protoReq.toByteArray());

    XMLMessageProducer producer = null;
    XMLMessageConsumer consumer = null;
    try {
      producer = session.getMessageProducer(new ProducerHandler());
      consumer = session.getMessageConsumer((XMLMessageListener) null);
      consumer.start();

      var requestor = session.createRequestor();
      var topic = JCSMPFactory.onlyInstance().createTopic("items/row");
      var msgRes = requestor.request(msgReq, replyTimeout, topic);
      var protoRes = ItemReqProto.parseFrom(((BytesMessage) msgRes).getData());

      return ItemReqDto.builder()
          .id(protoRes.getId())
          .categoryId(protoRes.getCategoryId().getNumber())
          .name(protoRes.getName())
          .amount(protoRes.getAmount())
          .price(protoRes.getPrice())
          .build();
    } catch (InvalidProtocolBufferException e) {
      log.error("", e);
    } finally {
      if (producer != null && !producer.isClosed()) producer.close();
      if (consumer != null && !consumer.isClosed()) consumer.stop();
    }

    return null;
  }

  public ItemReqsDto getItems(Integer page, Integer limit) throws JCSMPException {
    var msgReq = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
    msgReq.setDeliveryMode(DeliveryMode.DIRECT);

    var protoReq = ItemReqsPageProto.newBuilder().setPage(page).setLimit(limit).build();
    msgReq.setData(protoReq.toByteArray());

    XMLMessageProducer producer = null;
    XMLMessageConsumer consumer = null;
    try {
      producer = session.getMessageProducer(new ProducerHandler());
      consumer = session.getMessageConsumer((XMLMessageListener) null);
      consumer.start();

      var requestor = session.createRequestor();
      var topic = JCSMPFactory.onlyInstance().createTopic("items/paged");
      var msgRes = requestor.request(msgReq, replyTimeout, topic);
      var protoRes = ItemReqsProto.parseFrom(((BytesMessage) msgRes).getData());

      var protoList = protoRes.getItemsList();
      var dtoList = new ArrayList<ItemReqDto>();

      for (var item : protoList) {
        dtoList.add(
            ItemReqDto.builder()
                .id(item.getId())
                .categoryId(item.getCategoryIdValue())
                .name(item.getName())
                .amount(item.getAmount())
                .price(item.getPrice())
                .build());
      }

      return ItemReqsDto.builder().list(dtoList).build();
    } catch (InvalidProtocolBufferException e) {
      log.error("", e);
    } finally {
      if (producer != null && !producer.isClosed()) producer.close();
      if (consumer != null && !consumer.isClosed()) consumer.stop();
    }

    return null;
  }
}
