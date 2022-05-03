package com.downvoteit.springsolaceconsumerone.service;

import com.downvoteit.springgpb.ItemRequest;
import com.downvoteit.springsolaceconsumerone.exception.CheckedPersistenceException;
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

  public void saveItem(ItemRequest data) throws CheckedPersistenceException {
    repository.saveItem(data);
  }

  @Cacheable(value = "redis-item-cache")
  public ItemRequest getItem(String name) {
    return repository.getItem(name);
  }
}
