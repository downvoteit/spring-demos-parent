package com.downvoteit.springsolaceconsumerone.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
@ComponentScan(
    basePackages = {
      "com.downvoteit.springsolaceconsumerone.*",
      "com.downvoteit.springrediscommon.*"
    })
public class ScanConfig {}
