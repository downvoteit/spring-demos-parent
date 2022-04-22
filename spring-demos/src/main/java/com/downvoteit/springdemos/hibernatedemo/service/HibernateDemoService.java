package com.downvoteit.springdemos.hibernatedemo.service;

import com.downvoteit.springdemos.hibernatedemo.entity.Item;
import com.downvoteit.springdemos.hibernatedemo.entity.StoreItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.LongStream;

@Slf4j
@Service
@Profile({"hibernate-pg-demo", "hibernate-h2-demo"})
public class HibernateDemoService {
  private final EntityManagerFactory entityManagerFactory;

  @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
  private Integer batchSize;

  public HibernateDemoService(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  public boolean batchSaveItems() throws NoSuchAlgorithmException {
    var entityManager = entityManagerFactory.createEntityManager();
    var entityTransaction = entityManager.getTransaction();
    entityTransaction.begin();

    try {
      var query = entityManager.createQuery("delete from items");
      query.executeUpdate();

      entityManager.flush();
      entityManager.clear();

      var random = SecureRandom.getInstanceStrong();

      LongStream.rangeClosed(1, 6000)
          .forEach(
              index -> {
                var item =
                    new Item(null, "A" + index, random.nextInt() * 10, random.nextDouble() * 100);

                entityManager.persist(item);

                if (index > 0 && index % batchSize == 0) {
                  entityManager.flush();
                  // Clear PersistentContext (first level cache) to avoid getting an OOM
                  entityManager.clear();
                }
              });
    } finally {
      entityTransaction.commit();
      entityManager.close();
    }

    return true;
  }

  public boolean joinedTableSaveOtherItems() {
    var entityManager = entityManagerFactory.createEntityManager();
    var entityTransaction = entityManager.getTransaction();
    entityTransaction.begin();

    try {
      LongStream.rangeClosed(1, 200)
          .forEach(index -> entityManager.persist(new StoreItem("B" + index)));

      LongStream.rangeClosed(1, 300)
          .forEach(index -> entityManager.persist(new StoreItem("C" + index)));
    } finally {
      entityTransaction.commit();
      entityManager.close();
    }

    return true;
  }

  public int slowQueryGetItems() {
    var entityManager = entityManagerFactory.createEntityManager();
    var entityTransaction = entityManager.getTransaction();
    entityTransaction.begin();

    try {
      List<Item> list =
          entityManager
              .createQuery("select t from items t order by t.name desc", Item.class)
              .setFirstResult(1)
              .setMaxResults(3000)
              .getResultList();

      return list.size();
    } finally {
      entityTransaction.rollback();
      entityManager.close();
    }
  }
}
