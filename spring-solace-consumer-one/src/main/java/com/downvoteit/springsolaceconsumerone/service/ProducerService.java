package com.downvoteit.springsolaceconsumerone.service;

import com.downvoteit.springgpb.ItemRequest;
import com.solacesystems.jcsmp.*;
import com.downvoteit.springcommon.dto.ItemCorKeyDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("default")
public class ProducerService {
  private final Queue queueSecondaryRollback;

  public ProducerService(@Qualifier("queue-secondary-rollback") Queue queueSecondaryRollback) {
    this.queueSecondaryRollback = queueSecondaryRollback;
  }

  public void createItemRollbackMessage(
      ItemRequest data, XMLMessageProducer producerSecondaryRollback) {
    try {
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
