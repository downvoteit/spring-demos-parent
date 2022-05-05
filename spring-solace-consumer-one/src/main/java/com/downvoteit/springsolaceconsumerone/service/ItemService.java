package com.downvoteit.springsolaceconsumerone.service;

import com.downvoteit.springgpb.ItemReqProto;
import com.downvoteit.springgpb.ItemReqsProto;
import com.downvoteit.springsolacecommon.exception.CheckedPersistenceException;
import com.downvoteit.springsolaceconsumerone.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ItemService {
  private final ItemRepository repository;

  public ItemService(ItemRepository repository) {
    this.repository = repository;
  }

  public void saveItem(ItemReqProto data) throws CheckedPersistenceException {
    repository.saveItem(data);
  }

  @Cacheable(value = "redis-item-cache")
  public ItemReqProto getItem(String name) {
    return repository.getItem(name);
  }

  public ItemReqsProto getItems(Integer page, Integer limit) {
    return repository.getItems(page, limit);
  }
}
