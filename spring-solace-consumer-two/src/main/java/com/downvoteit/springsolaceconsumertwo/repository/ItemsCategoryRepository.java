package com.downvoteit.springsolaceconsumertwo.repository;

import com.downvoteit.springgpb.ItemReqProto;
import com.downvoteit.springhibernatecommon.entity.secondary.ItemsCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManagerFactory;

@Slf4j
@Repository
public class ItemsCategoryRepository {
  private final EntityManagerFactory factory;

  public ItemsCategoryRepository(EntityManagerFactory factory) {
    this.factory = factory;
  }

  public void updateCategory(ItemReqProto proto, boolean rollback) {
    var manager = factory.createEntityManager();
    var transaction = manager.getTransaction();

    try {
      transaction.begin();

      var row = manager.getReference(ItemsCategory.class, proto.getCategoryId().getNumber());

      var newAmount = 0;
      var newPrice = 0D;
      if (rollback) {
        newAmount = row.getAmount() - proto.getAmount();
        newPrice = row.getPrice() - proto.getPrice();
      } else {
        newAmount = row.getAmount() + proto.getAmount();
        newPrice = row.getPrice() + proto.getPrice();
      }

      row.setAmount(newAmount);
      row.setPrice(newPrice);

      manager.merge(row);
    } finally {
      transaction.commit();
      manager.close();
    }
  }
}
