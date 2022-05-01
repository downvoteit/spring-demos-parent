package com.downvoteit.springhibernate.service;

import com.downvoteit.springhibernate.entity.Item;
import com.downvoteit.springhibernate.entity.StoreItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.LongStream;

@Slf4j
@Service
public class ItemService {
  private final EntityManagerFactory entityManagerFactory;

  @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
  private Integer batchSize;

  public ItemService(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  public boolean batchSaveItems() throws NoSuchAlgorithmException {
    var manager = entityManagerFactory.createEntityManager();
    var transaction = manager.getTransaction();
    transaction.begin();

    try {
      var query = manager.createQuery("delete from items");
      query.executeUpdate();

      manager.flush();
      manager.clear();

      var random = SecureRandom.getInstanceStrong();

      LongStream.rangeClosed(1, 6000)
          .forEach(
              index -> {
                var item =
                    new Item(null, "A" + index, random.nextInt() * 10, random.nextDouble() * 100);

                manager.persist(item);

                if (index > 0 && index % batchSize == 0) {
                  manager.flush();
                  // Clear PersistentContext (first level cache) to avoid getting an OOM
                  manager.clear();
                }
              });
    } finally {
      transaction.commit();
      manager.close();
    }

    return true;
  }

  public boolean joinedTableSaveOtherItems() {
    var manager = entityManagerFactory.createEntityManager();
    var transaction = manager.getTransaction();
    transaction.begin();

    try {
      LongStream.rangeClosed(1, 200).forEach(index -> manager.persist(new StoreItem("B" + index)));

      LongStream.rangeClosed(1, 300).forEach(index -> manager.persist(new StoreItem("C" + index)));
    } finally {
      transaction.commit();
      manager.close();
    }

    return true;
  }

  public int slowQueryGetItems() {
    var manager = entityManagerFactory.createEntityManager();
    var transaction = manager.getTransaction();
    transaction.begin();

    try {
      List<Item> list =
          manager
              .createQuery("select t from items t order by t.name desc", Item.class)
              .setFirstResult(1)
              .setMaxResults(3000)
              .getResultList();

      return list.size();
    } finally {
      transaction.rollback();
      manager.close();
    }
  }
}
