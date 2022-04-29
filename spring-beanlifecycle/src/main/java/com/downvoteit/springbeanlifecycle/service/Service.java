package com.downvoteit.springbeanlifecycle.service;

import com.downvoteit.springbeanlifecycle.config.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/*
  Bean life cycle

  1. Bean Definition
  2. Bean Creation and Instantiate
  3. Post-initialization
  4. Populating Bean properties
  5. Ready to Serve
  6. Pre-destroy
  7. Bean Destroyed
*/
@Slf4j
@Component("serviceOne")
public class Service
    implements BeanNameAware,
        BeanClassLoaderAware,
        BeanFactoryAware,
        BeanPostProcessor,
        ApplicationContextAware,
        InitializingBean,
        DisposableBean {
  private final Properties properties;

  public Service(Properties properties) {
    this.properties = properties;
  }

  @Override
  public void setBeanName(String name) {
    log.info("1. Bean setBeanName() is called for {}", name);
  }

  @Override
  public void setBeanClassLoader(ClassLoader classLoader) {
    log.info("2-1. Bean setBeanClassLoader() is called");
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    log.info("2-2. Bean setBeanFactory() is called");
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    log.info("2-3. Bean setApplicationContext() is called");
  }

  @Override
  public void destroy() {
    log.info("7. Bean destroy() is called");
  }

  @Override
  public void afterPropertiesSet() {
    log.info("4. Bean afterPropertiesSet() is called");
  }

  @PostConstruct
  public void postConstructMethod() {
    log.info("3. Bean postConstructMethod() is called");
  }

  @PreDestroy
  public void preDestroy() {
    log.info("6. Bean preDestroy() is called");
  }

  public Properties getProperties() {
    log.info("5. Bean getProperties() is called ");

    return properties;
  }
}
