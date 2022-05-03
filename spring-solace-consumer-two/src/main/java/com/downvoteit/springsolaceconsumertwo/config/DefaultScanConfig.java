package com.downvoteit.springsolaceconsumertwo.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
    basePackages = {
      "com.downvoteit.springsolaceconsumertwo.*",
      "com.downvoteit.springsolacecommon.*",
      "com.downvoteit.springhibernatecommon.*"
    })
@EntityScan(basePackages = {"com.downvoteit.springhibernatecommon.entity.secondary"})
public class DefaultScanConfig {}
