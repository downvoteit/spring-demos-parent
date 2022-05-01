package com.downvoteit.springsolaceproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
    basePackages = {
      "com.downvoteit.springsolaceproducer.*",
      "com.downvoteit.springsolacecommon.*",
    })
public class SpringSolaceProducerApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringSolaceProducerApplication.class, args);
  }
}
