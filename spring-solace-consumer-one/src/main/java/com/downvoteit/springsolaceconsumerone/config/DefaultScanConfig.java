package com.downvoteit.springsolaceconsumerone.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("default")
@ComponentScan(
    basePackages = {
      "com.downvoteit.springsolaceconsumerone.*",
      "com.downvoteit.springsolacecommon.*",
      "com.downvoteit.springrediscommon.*",
      "com.downvoteit.springhibernatecommon.*"
    })
@EntityScan(basePackages = {"com.downvoteit.springhibernatecommon.entity.primary"})
public class DefaultScanConfig {}
