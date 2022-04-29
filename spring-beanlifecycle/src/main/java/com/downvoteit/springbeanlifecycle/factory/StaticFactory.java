package com.downvoteit.springbeanlifecycle.factory;

import com.downvoteit.springbeanlifecycle.config.Properties;
import com.downvoteit.springbeanlifecycle.service.Service;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class StaticFactory {
  private StaticFactory() {}

  @Bean("serviceThree")
  public static Service getInstanceMain(Properties properties) {
    return new Service(properties);
  }
}
