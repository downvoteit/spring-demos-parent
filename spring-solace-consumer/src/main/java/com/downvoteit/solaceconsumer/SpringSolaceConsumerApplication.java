package com.downvoteit.solaceconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.downvoteit.*")
public class SpringSolaceConsumerApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringSolaceConsumerApplication.class, args);
  }
}
