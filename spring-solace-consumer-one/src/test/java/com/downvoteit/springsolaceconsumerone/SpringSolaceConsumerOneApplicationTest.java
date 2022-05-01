package com.downvoteit.springsolaceconsumerone;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringSolaceConsumerOneApplicationTest {
  @Test
  void contextLoads() {
    assertDoesNotThrow(() -> {});
  }
}
