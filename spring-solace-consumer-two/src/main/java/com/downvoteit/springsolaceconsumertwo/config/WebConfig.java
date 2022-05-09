package com.downvoteit.springsolaceconsumertwo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class WebConfig {
  @Value("${webclient.allowedOrigin:http://localhost:8002}")
  private String allowedOrigin;

  @Bean
  public CorsWebFilter corsFilter() {
    var config = new CorsConfiguration();

    config.setAllowCredentials(true);
    config.addAllowedOrigin(allowedOrigin);
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");

    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return new CorsWebFilter(source);
  }
}
