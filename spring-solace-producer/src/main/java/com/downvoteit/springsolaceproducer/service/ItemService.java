package com.downvoteit.springsolaceproducer.service;

import com.downvoteit.springcommon.dto.ItemFilterDto;
import com.downvoteit.springcommon.dto.ItemReqDto;
import com.downvoteit.springcommon.dto.ItemReqsDto;
import com.downvoteit.springcommon.dto.ResDto;
import com.solacesystems.jcsmp.JCSMPException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ItemService {
  private final PersistentPubSubService persistentPubSubService;
  private final DirectRequestReplyService directRequestReplyService;
  private final SemiPersistentRequestReplyService semiPersistentRequestReplyService;

  public ItemService(
      PersistentPubSubService persistentPubSubService,
      DirectRequestReplyService directRequestReplyService,
      SemiPersistentRequestReplyService semiPersistentRequestReplyService) {
    this.persistentPubSubService = persistentPubSubService;
    this.directRequestReplyService = directRequestReplyService;
    this.semiPersistentRequestReplyService = semiPersistentRequestReplyService;
  }

  public ResDto createItem(ItemReqDto dto) throws JCSMPException {
    return persistentPubSubService.createItem(dto);
  }

  public ItemReqDto getItem(ItemFilterDto dto) throws JCSMPException {
    return directRequestReplyService.getItem(dto);
  }

  public ItemReqsDto getItems(Integer page, Integer limit) throws JCSMPException {
    return directRequestReplyService.getItems(page, limit);
  }

  public ResDto deleteItem(Integer id) throws JCSMPException {
    return semiPersistentRequestReplyService.deleteItem(id);
  }
}
