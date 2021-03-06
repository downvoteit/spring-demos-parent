package com.downvoteit.springsolaceconsumertwo.service;

import com.downvoteit.springproto.ItemReqProto;
import com.downvoteit.springhibernatecommon.entity.secondary.ItemsCategory;
import com.downvoteit.springsolaceconsumertwo.repository.ItemsCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemsCategoryService {
  private final ItemsCategoryRepository repository;

  public ItemsCategoryService(ItemsCategoryRepository repository) {
    this.repository = repository;
  }

  public void updateCategory(ItemReqProto data, boolean undo) {
    repository.updateCategory(data, undo);
  }

  public List<ItemsCategory> getItemsCategory(Integer page, Integer limit) {
    return repository.getItemsCategory(page, limit);
  }
}
