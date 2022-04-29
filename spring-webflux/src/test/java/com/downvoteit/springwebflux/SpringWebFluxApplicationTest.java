package com.downvoteit.springwebflux;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringWebFluxApplicationTest {
  @Test
  void contextLoads() {
    assertDoesNotThrow(() -> {});
  }
}
