package com.downvoteit.springbeanlifecycle;

import com.downvoteit.springbeanlifecycle.service.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Slf4j
@SpringBootApplication
public class SpringBeanLifecycleApplication implements ApplicationContextAware {
  private static ApplicationContext applicationContext;

  public static void main(String[] args) {
    SpringApplication.run(SpringBeanLifecycleApplication.class, args);

    var serviceOne = (Service) applicationContext.getBean("serviceOne");
    var serviceTwo = (Service) applicationContext.getBean("serviceTwo");
    var serviceThree = (Service) applicationContext.getBean("serviceThree");

    log.info("{}", serviceOne.getProperties());
    log.info("{}", serviceTwo.getProperties());
    log.info("{}", serviceThree.getProperties());
  }

  private static void setApplicationContextStatic(ApplicationContext applicationContext) {
    SpringBeanLifecycleApplication.applicationContext = applicationContext;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    setApplicationContextStatic(applicationContext);
  }
}
