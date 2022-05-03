package com.downvoteit.springsolaceproducer.service;

import com.solacesystems.jcsmp.JCSMPException;
import com.downvoteit.springcommon.dto.ItemRequestDto;
import com.downvoteit.springcommon.dto.ItemResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ItemService {
  private final ProducerService producerService;
  private final ReplyService replyService;

  public ItemService(ProducerService producerService, ReplyService replyService) {
    this.producerService = producerService;
    this.replyService = replyService;
  }

  public ItemResponseDto createItem(ItemRequestDto itemRequestDto) throws JCSMPException {
    return ItemResponseDto.builder()
        .id(producerService.createItem(itemRequestDto))
        .message(
            String.format(
                "Creation successful, created an item with name %s", itemRequestDto.getName()))
        .build();
  }

  public ItemRequestDto getItem(String name) throws JCSMPException {
    return replyService.getItem(name);
  }
}
