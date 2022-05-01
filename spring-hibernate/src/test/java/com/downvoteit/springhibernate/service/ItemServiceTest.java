package com.downvoteit.springhibernate.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemServiceTest {
  @Autowired private ItemService itemService;

  @Test
  @Order(1)
  void batchSaveItems_mustSaveItems_PositiveIntegrationTest() {
    boolean isSuccessful = assertDoesNotThrow(() -> itemService.batchSaveItems());

    assertTrue(isSuccessful);
  }

  @Test
  @Order(2)
  void joinedTableSaveOtherItems_mustSaveOtherItems_PositiveIntegrationTest() {
    boolean isSuccessful = itemService.joinedTableSaveOtherItems();

    assertTrue(isSuccessful);
  }

  @Test
  @Order(3)
  void slowQueryGetItems_mustLogSlowQuery_PositiveIntegrationTest() {
    int size = itemService.slowQueryGetItems();

    assertTrue(size > 0);
  }
}
