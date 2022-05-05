package com.downvoteit.springsolaceconsumertwo.service;

import com.downvoteit.springgpb.ItemReqProto;
import com.downvoteit.springsolaceconsumertwo.repository.ItemCategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemCategoryService {
  private final ItemCategoryRepository repository;

  public ItemCategoryService(ItemCategoryRepository repository) {
    this.repository = repository;
  }

  public void updateCategory(ItemReqProto data, boolean rollback) {
    repository.updateCategory(data, rollback);
  }
}
