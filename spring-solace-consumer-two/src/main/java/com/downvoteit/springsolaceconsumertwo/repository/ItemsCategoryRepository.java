package com.downvoteit.springsolaceconsumertwo.repository;

import com.downvoteit.springproto.ItemReqProto;
import com.downvoteit.springhibernatecommon.entity.secondary.ItemsCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class ItemsCategoryRepository {
  private final EntityManagerFactory factory;

  public ItemsCategoryRepository(EntityManagerFactory factory) {
    this.factory = factory;
  }

  public void updateCategory(ItemReqProto proto, boolean undo) {
    var manager = factory.createEntityManager();
    var transaction = manager.getTransaction();

    try {
      transaction.begin();

      var row = manager.getReference(ItemsCategory.class, proto.getCategoryId().getNumber());

      var newAmount = 0;
      var newPrice = 0D;
      if (undo) {
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

  public List<ItemsCategory> getItemsCategory(Integer page, Integer limit) {
    var manager = factory.createEntityManager();
    var transaction = manager.getTransaction();

    try {
      transaction.begin();

      List<ItemsCategory> pagedList =
          manager
              .createQuery("select t from items_categories t order by t.id", ItemsCategory.class)
              .setFirstResult(page)
              .setMaxResults(limit)
              .getResultList();

      if (pagedList.isEmpty()) pagedList = new ArrayList<>();

      return pagedList;
    } finally {
      transaction.rollback();
      manager.close();
    }
  }
}
