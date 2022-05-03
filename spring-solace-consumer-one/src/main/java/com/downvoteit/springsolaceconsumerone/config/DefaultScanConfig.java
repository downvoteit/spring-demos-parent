package com.downvoteit.springsolaceconsumerone.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("default")
@ComponentScan(
    basePackages = {
      "com.downvoteit.springsolaceconsumerone.*",
      "com.downvoteit.springsolacecommon.*",
      "com.downvoteit.springrediscommon.*"
    })
public class DefaultScanConfig {}
