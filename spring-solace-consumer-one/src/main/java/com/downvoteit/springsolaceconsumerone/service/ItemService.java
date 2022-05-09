package com.downvoteit.springsolaceconsumerone.service;

import com.downvoteit.springcommon.dto.ItemFilterDto;
import com.downvoteit.springgpb.ItemReqProto;
import com.downvoteit.springgpb.ItemReqsProto;
import com.downvoteit.springsolacecommon.exception.CheckedPersistenceException;
import com.downvoteit.springsolaceconsumerone.repository.ItemRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemService {
  private final ItemRepository repository;

  public ItemService(ItemRepository repository) {
    this.repository = repository;
  }

  public void saveItem(ItemReqProto data) throws CheckedPersistenceException {
    repository.saveItem(data);
  }

  //  @Cacheable(value = "redis-item-cache")
  public ItemReqProto getItem(ItemFilterDto dto) {
    return repository.getItem(dto);
  }

  public ItemReqsProto getItems(Integer page, Integer limit) {
    return repository.getItems(page, limit);
  }

  //  @CacheEvict(value = "redis-item-cache", allEntries = true)
  public ItemReqProto deleteItem(Integer id) {
    return repository.deleteItem(id);
  }
}
