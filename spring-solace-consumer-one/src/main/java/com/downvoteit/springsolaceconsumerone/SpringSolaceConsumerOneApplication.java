package com.downvoteit.springsolaceconsumerone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
    basePackages = {
      "com.downvoteit.springsolaceconsumerone.*",
      "com.downvoteit.springsolacecommon.*",
    })
public class SpringSolaceConsumerOneApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringSolaceConsumerOneApplication.class, args);
  }
}
