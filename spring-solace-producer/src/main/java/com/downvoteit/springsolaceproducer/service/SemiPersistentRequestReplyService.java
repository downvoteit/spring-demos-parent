package com.downvoteit.springsolaceproducer.service;

import com.downvoteit.springcommon.dto.ResDto;
import com.downvoteit.springgpb.ItemReqProto;
import com.downvoteit.springgpb.ReqProto;
import com.downvoteit.springsolacecommon.handler.ProducerHandler;
import com.downvoteit.springsolacecommon.properties.AppProperties;
import com.google.protobuf.InvalidProtocolBufferException;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SemiPersistentRequestReplyService {
  private final JCSMPSession session;
  private final Queue deleteItemOltpQueue;

  @Value("${topics.timout:500}")
  private Integer replyTimeout;

  public SemiPersistentRequestReplyService(
      JCSMPSession session,
      @Qualifier(AppProperties.DeleteItemOltp.QUEUE) Queue deleteItemOltpQueue) {
    this.session = session;
    this.deleteItemOltpQueue = deleteItemOltpQueue;
  }

  public ResDto deleteItem(Integer id) throws JCSMPException {
    var msgReq = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
    msgReq.setDeliveryMode(DeliveryMode.DIRECT);

    var protoReq = ReqProto.newBuilder().setId(id).build();
    msgReq.setData(protoReq.toByteArray());

    XMLMessageProducer producer = null;
    XMLMessageConsumer consumer = null;
    try {
      producer = session.getMessageProducer(new ProducerHandler());
      consumer = session.getMessageConsumer((XMLMessageListener) null);
      consumer.start();

      var requestor = session.createRequestor();
      var msgRes = requestor.request(msgReq, replyTimeout, deleteItemOltpQueue);
      var protoRes = ItemReqProto.parseFrom(((BytesMessage) msgRes).getData());

      return ResDto.builder().id(protoRes.getId()).message("Deletion successful").build();
    } catch (InvalidProtocolBufferException e) {
      log.error("", e);
    } finally {
      if (producer != null && !producer.isClosed()) producer.close();
      if (consumer != null && !consumer.isClosed()) consumer.stop();
    }

    return null;
  }
}
