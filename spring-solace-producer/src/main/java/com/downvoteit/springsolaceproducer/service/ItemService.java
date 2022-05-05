package com.downvoteit.springsolaceproducer.service;

import com.downvoteit.springcommon.dto.ItemReqDto;
import com.downvoteit.springcommon.dto.ItemReqsDto;
import com.downvoteit.springcommon.dto.ItemResDto;
import com.solacesystems.jcsmp.JCSMPException;
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

  public ItemResDto createItem(ItemReqDto dto) throws JCSMPException {
    var message = String.format("Creation successful, created an item with name %s", dto.getName());

    return ItemResDto.builder().id(producerService.createItem(dto)).message(message).build();
  }

  public ItemReqDto getItem(String name) throws JCSMPException {
    return replyService.getItem(name);
  }

  public ItemReqsDto getItems(Integer page, Integer limit) throws JCSMPException {
    return replyService.getItems(page, limit);
  }
}
