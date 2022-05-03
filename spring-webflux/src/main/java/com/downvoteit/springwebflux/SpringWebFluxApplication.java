package com.downvoteit.springwebflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SpringWebFluxApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringWebFluxApplication.class, args);
  }
}
