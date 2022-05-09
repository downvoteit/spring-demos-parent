package com.downvoteit.springsolaceconsumertwo.service;

import com.downvoteit.springgpb.ItemReqProto;
import com.downvoteit.springsolaceconsumertwo.repository.ItemsCategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemsCategoryService {
  private final ItemsCategoryRepository repository;

  public ItemsCategoryService(ItemsCategoryRepository repository) {
    this.repository = repository;
  }

  public void updateCategory(ItemReqProto data, boolean rollback) {
    repository.updateCategory(data, rollback);
  }
}
