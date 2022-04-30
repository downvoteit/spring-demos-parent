package com.downvoteit.springwebflux.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
  @Value("${webclient.baseUrl:http://localhost:7004}")
  private String baseUrl;

  @Bean
  public WebClient createWebClient() {
    return WebClient.builder().baseUrl(baseUrl).build();
  }
}
