package com.downvoteit.springsolaceconsumerone.repository;

import com.downvoteit.springgpb.ItemRequest;
import com.downvoteit.springhibernatecommon.entity.primary.Category;
import com.downvoteit.springhibernatecommon.entity.primary.Item;
import com.downvoteit.springsolaceconsumerone.exception.CheckedPersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManagerFactory;

@Slf4j
@Repository
public class ItemRepository {
  private final EntityManagerFactory factory;

  public ItemRepository(EntityManagerFactory factory) {
    this.factory = factory;
  }

  public void saveItem(ItemRequest data) throws CheckedPersistenceException {
    try {
      var manager = factory.createEntityManager();
      var transaction = manager.getTransaction();

      try {
        transaction.begin();

        var category = manager.find(Category.class, data.getCategoryId());

        var item =
            Item.builder()
                .category(category)
                .name(data.getName())
                .amount(data.getAmount())
                .price(data.getPrice())
                .build();

        manager.persist(item);
      } finally {
        transaction.commit();
        manager.close();
      }
    } catch (RuntimeException e) {
      throw new CheckedPersistenceException("Cannot save an item", e);
    }
  }

  public ItemRequest getItem(String name) {
    var manager = factory.createEntityManager();
    var transaction = manager.getTransaction();

    try {
      transaction.begin();

      var data =
          manager
              .createQuery("select t from items t where t.name = :name", Item.class)
              .setParameter("name", name)
              .getSingleResult();

      return ItemRequest.newBuilder()
          .setId(data.getId())
          .setCategoryId(data.getCategory().getId())
          .setName(data.getName())
          .setAmount(data.getAmount())
          .setPrice(data.getPrice())
          .build();
    } finally {
      transaction.rollback();
      manager.close();
    }
  }
}
