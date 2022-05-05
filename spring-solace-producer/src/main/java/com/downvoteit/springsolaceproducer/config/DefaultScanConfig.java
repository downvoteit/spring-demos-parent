package com.downvoteit.springsolaceproducer.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
    basePackages = {
      "com.downvoteit.springsolaceproducer.*",
      "com.downvoteit.springsolacecommon.*",
    })
@EntityScan(basePackages = {"com.downvoteit.springhibernatecommon.entity.primary"})
public class DefaultScanConfig {}
