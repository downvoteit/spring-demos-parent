package com.downvoteit.springdemos.hibernatedemo.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("hibernate-pg-demo")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HibernateDemoServiceTest {
  @Autowired private HibernateDemoService hibernateDemoService;

  @Test
  @Order(1)
  void batchSaveItems_mustSaveItems_PositiveIntegrationTest() {
    boolean isSuccessful = assertDoesNotThrow(() -> hibernateDemoService.batchSaveItems());

    assertTrue(isSuccessful);
  }

  @Test
  @Order(2)
  void joinedTableSaveOtherItems_mustSaveOtherItems_PositiveIntegrationTest() {
    boolean isSuccessful = hibernateDemoService.joinedTableSaveOtherItems();

    assertTrue(isSuccessful);
  }

  @Test
  @Order(3)
  void slowQueryGetItems_mustLogSlowQuery_PositiveIntegrationTest() {
    int size = hibernateDemoService.slowQueryGetItems();

    assertTrue(size > 0);
  }
}
