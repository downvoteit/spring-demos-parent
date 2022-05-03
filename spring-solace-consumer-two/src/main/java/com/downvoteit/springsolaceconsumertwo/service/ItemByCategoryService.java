package com.downvoteit.springsolaceconsumertwo.service;

import com.downvoteit.springgpb.ItemRequest;
import com.downvoteit.springsolaceconsumertwo.repository.ItemByCategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemByCategoryService {
  private final ItemByCategoryRepository repository;

  public ItemByCategoryService(ItemByCategoryRepository repository) {
    this.repository = repository;
  }

  public void updateCategory(ItemRequest data, boolean rollback) {
    repository.updateCategory(data, rollback);
  }
}
