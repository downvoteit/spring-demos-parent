package com.downvoteit.springsolaceconsumerone.service;

import com.downvoteit.springcommon.dto.ItemCorKeyDto;
import com.downvoteit.springgpb.ItemReqProto;
import com.downvoteit.springsolacecommon.config.SharedProps;
import com.downvoteit.springsolacecommon.handler.ProducerHandler;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("default")
public class ProducerService {
  private final JCSMPSession session;
  private final Queue queueSecondaryRollback;

  public ProducerService(
      JCSMPSession session, @Qualifier(SharedProps.CreateItemOlap.Rollback.QUEUE) Queue queueSecondaryRollback) {
    this.session = session;
    this.queueSecondaryRollback = queueSecondaryRollback;
  }

  public void createItemRollbackMessage(ItemReqProto data) {
    try {
      var producerSecondaryRollback = session.getMessageProducer(new ProducerHandler() {});

      var rollbackMessage = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
      rollbackMessage.setDeliveryMode(DeliveryMode.PERSISTENT);

      var corKeyDto = ItemCorKeyDto.builder().id(data.getId()).build();

      rollbackMessage.setCorrelationKey(corKeyDto);
      rollbackMessage.setData(data.toByteArray());

      log.info("Produced rollback: \n{}", data);

      producerSecondaryRollback.send(rollbackMessage, queueSecondaryRollback);
    } catch (JCSMPException e) {
      log.error("", e);
    }
  }
}
