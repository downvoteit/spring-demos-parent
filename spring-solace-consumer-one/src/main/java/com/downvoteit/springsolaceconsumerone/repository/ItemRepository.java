package com.downvoteit.springsolaceconsumerone.repository;

import com.downvoteit.springgpb.CategoryProto;
import com.downvoteit.springgpb.ItemReqProto;
import com.downvoteit.springgpb.ItemReqsProto;
import com.downvoteit.springhibernatecommon.entity.primary.Category;
import com.downvoteit.springhibernatecommon.entity.primary.Item;
import com.downvoteit.springsolacecommon.exception.CheckedPersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import java.util.List;

@Slf4j
@Repository
public class ItemRepository {
  private final EntityManagerFactory factory;

  public ItemRepository(EntityManagerFactory factory) {
    this.factory = factory;
  }

  public void saveItem(ItemReqProto data) throws CheckedPersistenceException {
    try {
      var manager = factory.createEntityManager();
      var transaction = manager.getTransaction();

      try {
        transaction.begin();

        var category = manager.find(Category.class, data.getCategoryId().getNumber());

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

  public ItemReqProto getItem(String name) {
    var manager = factory.createEntityManager();
    var transaction = manager.getTransaction();

    try {
      transaction.begin();

      var data =
          manager
              .createQuery("select t from items t where t.name = :name", Item.class)
              .setParameter("name", name)
              .getSingleResult();

      return ItemReqProto.newBuilder()
          .setId(data.getId())
          .setCategoryId(CategoryProto.forNumber(data.getCategory().getId()))
          .setName(data.getName())
          .setAmount(data.getAmount())
          .setPrice(data.getPrice())
          .build();
    } finally {
      transaction.rollback();
      manager.close();
    }
  }

  public ItemReqsProto getItems(Integer page, Integer limit) {
    var manager = factory.createEntityManager();
    var transaction = manager.getTransaction();

    try {
      transaction.begin();

      List<Item> list =
          manager
              .createQuery("select t from items t order by t.name", Item.class)
              .setFirstResult(page)
              .setMaxResults(limit)
              .getResultList();

      if (list.isEmpty()) throw new PersistenceException("No data found");

      var proto = ItemReqsProto.newBuilder();

      for (var item : list) {
        var temp =
            ItemReqProto.newBuilder()
                .setId(item.getId())
                .setCategoryId(CategoryProto.forNumber(item.getCategory().getId()))
                .setName(item.getName())
                .setAmount(item.getAmount())
                .setPrice(item.getPrice())
                .build();

        proto.addItems(temp);
      }

      return proto.build();
    } finally {
      transaction.rollback();
      manager.close();
    }
  }
}
