package com.downvoteit.springsolaceproducer.service;

import com.downvoteit.springcommon.dto.ItemReqDto;
import com.downvoteit.springcommon.dto.ItemReqsDto;
import com.downvoteit.springgpb.ItemReqNameProto;
import com.downvoteit.springgpb.ItemReqProto;
import com.downvoteit.springgpb.ItemReqsPageProto;
import com.downvoteit.springgpb.ItemReqsProto;
import com.downvoteit.springsolacecommon.config.SharedProps;
import com.downvoteit.springsolacecommon.handler.ProducerHandler;
import com.google.protobuf.InvalidProtocolBufferException;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class ReplyService {
  private final JCSMPSession session;
  private final Topic getItemOltpQueueTopic;
  private final Topic getItemsOltpQueueTopic;

  @Value("${topics.timout:1500}")
  private Integer replyTimeout;

  public ReplyService(
      JCSMPSession session,
      @Qualifier(SharedProps.GetItemOltp.TOPIC) Topic getItemOltpQueueTopic,
      @Qualifier(SharedProps.GetItemsOltp.TOPIC) Topic getItemsOltpQueueTopic) {
    this.session = session;
    this.getItemOltpQueueTopic = getItemOltpQueueTopic;
    this.getItemsOltpQueueTopic = getItemsOltpQueueTopic;
  }

  public ItemReqDto getItem(String name) throws JCSMPException {
    BytesMessage message = prepareGetItemMessage(name);

    @SuppressWarnings("unused")
    XMLMessageProducer producer = null;
    XMLMessageConsumer consumer = null;
    try {
      producer = session.getMessageProducer(new ProducerHandler());
      consumer = session.getMessageConsumer((XMLMessageListener) null);

      consumer.start();

      var requestor = session.createRequestor();
      var reply = requestor.request(message, replyTimeout, getItemOltpQueueTopic);

      log.info("Reply event scheduled for: {}", name);

      var bytes = ((BytesMessage) reply).getData();
      var data = ItemReqProto.parseFrom(bytes);

      log.info("Reply event data: \n{}", data);

      return ItemReqDto.builder()
          .id(data.getId())
          .categoryId(data.getCategoryId().getNumber())
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

    var data = ItemReqNameProto.newBuilder().setName(name).build();

    message.setData(data.toByteArray());

    return message;
  }

  public ItemReqsDto getItems(Integer page, Integer limit) throws JCSMPException {
    BytesMessage message = prepareGetItemsMessage(page, limit);

    @SuppressWarnings("unused")
    XMLMessageProducer producer = null;
    XMLMessageConsumer consumer = null;
    try {
      producer = session.getMessageProducer(new ProducerHandler());
      consumer = session.getMessageConsumer((XMLMessageListener) null);

      consumer.start();

      var requestor = session.createRequestor();
      var reply = requestor.request(message, replyTimeout, getItemsOltpQueueTopic);

      log.info("Reply event scheduled for, page: {}, limit: {}", page, limit);

      var bytes = ((BytesMessage) reply).getData();
      var data = ItemReqsProto.parseFrom(bytes);

      log.info("Reply event data: \n{}", data);

      var gpbList = data.getItemsList();
      var dtoList = new ArrayList<ItemReqDto>();

      for (var item : gpbList) {
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
    } catch (JCSMPException e) {
      log.warn("Reply event timeout elapsed for, page: {}, limit: {}", page, limit);

      throw e;
    } catch (InvalidProtocolBufferException e) {
      log.error("", e);
    } finally {
      if (consumer != null && !consumer.isClosed()) consumer.stop();
    }

    return null;
  }

  private BytesMessage prepareGetItemsMessage(int page, int limit) {
    var message = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
    message.setDeliveryMode(DeliveryMode.DIRECT);

    var data = ItemReqsPageProto.newBuilder().setPage(page).setLimit(limit).build();

    message.setData(data.toByteArray());

    return message;
  }
}
