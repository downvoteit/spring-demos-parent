package com.downvoteit.springhibernate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
    basePackages = {"com.downvoteit.springhibernate.*", "com.downvoteit.springhibernatecommon.*"})
@EntityScan(
    basePackages = {
      "com.downvoteit.springhibernate.*",
      "com.downvoteit.springhibernatecommon.entity.primary"
    })
public class SpringHibernateApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringHibernateApplication.class, args);
  }
}
