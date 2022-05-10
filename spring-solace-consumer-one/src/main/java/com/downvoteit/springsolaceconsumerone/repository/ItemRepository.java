package com.downvoteit.springsolaceconsumerone.repository;

import com.downvoteit.springcommon.dto.ItemFilterDto;
import com.downvoteit.springproto.CategoryProto;
import com.downvoteit.springproto.ItemReqProto;
import com.downvoteit.springproto.ItemReqsProto;
import com.downvoteit.springhibernatecommon.entity.primary.Category;
import com.downvoteit.springhibernatecommon.entity.primary.Item;
import com.downvoteit.springsolacecommon.exception.CheckedPersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class ItemRepository {
  private final EntityManagerFactory factory;

  public ItemRepository(EntityManagerFactory factory) {
    this.factory = factory;
  }

  public void saveItem(ItemReqProto proto) throws CheckedPersistenceException {
    try {
      var manager = factory.createEntityManager();
      var transaction = manager.getTransaction();

      try {
        transaction.begin();

        var ref = manager.find(Category.class, proto.getCategoryId().getNumber());

        var row =
            Item.builder()
                .category(ref)
                .name(proto.getName())
                .amount(proto.getAmount())
                .price(proto.getPrice())
                .build();

        manager.persist(row);
      } finally {
        transaction.commit();
        manager.close();
      }
    } catch (RuntimeException e) {
      throw new CheckedPersistenceException("Cannot save an item", e);
    }
  }

  public ItemReqProto getItem(ItemFilterDto dto) {
    var manager = factory.createEntityManager();
    var transaction = manager.getTransaction();

    try {
      transaction.begin();

      var row =
          manager
              .createQuery(
                  "select t from items t where t.category.id = :categoryId and t.name = :name",
                  Item.class)
              .setParameter("categoryId", dto.getCategoryId())
              .setParameter("name", dto.getName())
              .getSingleResult();

      return ItemReqProto.newBuilder()
          .setId(row.getId())
          .setCategoryId(CategoryProto.forNumber(row.getCategory().getId()))
          .setName(row.getName())
          .setAmount(row.getAmount())
          .setPrice(row.getPrice())
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

      List<Item> pagedList =
          manager
              .createQuery("select t from items t order by t.id", Item.class)
              .setFirstResult(page)
              .setMaxResults(limit)
              .getResultList();

      if (pagedList.isEmpty()) pagedList = new ArrayList<>();

      var protoList = ItemReqsProto.newBuilder();

      for (var row : pagedList) {
        var protoRow =
            ItemReqProto.newBuilder()
                .setId(row.getId())
                .setCategoryId(CategoryProto.forNumber(row.getCategory().getId()))
                .setName(row.getName())
                .setAmount(row.getAmount())
                .setPrice(row.getPrice())
                .build();

        protoList.addItems(protoRow);
      }

      manager.flush();
      manager.clear();

      return protoList.build();
    } finally {
      transaction.rollback();
      manager.close();
    }
  }

  public ItemReqProto deleteItem(Integer id) {
    var manager = factory.createEntityManager();
    var transaction = manager.getTransaction();

    try {
      transaction.begin();

      var row = manager.find(Item.class, id);

      manager
          .createQuery("delete from items t where t.id = :id")
          .setParameter("id", id)
          .executeUpdate();

      manager.flush();
      manager.clear();

      return ItemReqProto.newBuilder()
          .setId(row.getId())
          .setCategoryId(CategoryProto.forNumber(row.getCategory().getId()))
          .setName(row.getName())
          .setAmount(row.getAmount())
          .setPrice(row.getPrice())
          .build();
    } finally {
      transaction.commit();
      manager.close();
    }
  }
}
