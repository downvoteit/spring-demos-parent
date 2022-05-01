package com.downvoteit.springsolaceconsumertwo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
    basePackages = {
      "com.downvoteit.springsolaceconsumertwo.*",
      "com.downvoteit.springsolacecommon.*",
    })
public class SpringSolaceConsumerTwoApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringSolaceConsumerTwoApplication.class, args);
  }
}
