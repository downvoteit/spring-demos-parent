package com.downvoteit.springwebflux.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfig {
  @Value("${cache.item.name:caffeine-item-cache}")
  private String itemCacheNameProperty;

  @Bean
  public String itemCacheName() {
    return itemCacheNameProperty;
  }
}
