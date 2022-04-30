package com.downvoteit.springsolaceproducer.service;

import com.solacesystems.jcsmp.JCSMPException;
import dto.ItemRequestDto;
import dto.ItemResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ItemService {
  private final ProducerService producerService;

  public ItemService(ProducerService producerService) {
    this.producerService = producerService;
  }

  public ItemResponseDto createItem(String mode, ItemRequestDto itemRequestDto)
      throws JCSMPException {
    return ItemResponseDto.builder()
        .id(
            mode.equals("queue")
                ? producerService.sendQueue(itemRequestDto)
                : producerService.sendTopic(itemRequestDto))
        .message("Created")
        .build();
  }
}
