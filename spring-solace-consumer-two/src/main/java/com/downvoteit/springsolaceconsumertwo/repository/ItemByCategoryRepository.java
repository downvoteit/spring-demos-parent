package com.downvoteit.springsolaceconsumertwo.repository;

import com.downvoteit.springgpb.ItemRequest;
import com.downvoteit.springsolaceconsumertwo.entity.ItemsByCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManagerFactory;

@Slf4j
@Repository
public class ItemByCategoryRepository {
  private final EntityManagerFactory factory;

  public ItemByCategoryRepository(EntityManagerFactory factory) {
    this.factory = factory;
  }

  public void updateCategory(ItemRequest data, boolean rollback) {
    var manager = factory.createEntityManager();
    var transaction = manager.getTransaction();

    try {
      transaction.begin();

      var itemByCategory = manager.getReference(ItemsByCategory.class, data.getCategoryId());

      var newAmount = 0;
      var newPrice = 0D;
      if (rollback) {
        newAmount = itemByCategory.getAmount() - data.getAmount();
        newPrice = itemByCategory.getPrice() - data.getPrice();
      } else {
        newAmount = itemByCategory.getAmount() + data.getAmount();
        newPrice = itemByCategory.getPrice() + data.getPrice();
      }

      itemByCategory.setAmount(newAmount);
      itemByCategory.setPrice(newPrice);

      manager.merge(itemByCategory);
    } finally {
      transaction.commit();
      manager.close();
    }
  }
}
