package com.downvoteit.springsolaceconsumertwo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringSolaceConsumerTwoApplicationTest {
  @Test
  void contextLoads() {
    assertDoesNotThrow(() -> {});
  }
}
