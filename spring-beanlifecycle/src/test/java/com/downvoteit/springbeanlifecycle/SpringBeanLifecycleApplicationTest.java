package com.downvoteit.springbeanlifecycle;

import com.downvoteit.springbeanlifecycle.config.Properties;
import com.downvoteit.springbeanlifecycle.factory.InstanceFactory;
import com.downvoteit.springbeanlifecycle.factory.StaticFactory;
import com.downvoteit.springbeanlifecycle.service.Service;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpringBeanLifecycleApplicationTest {
  @Test
  void main_mustRegisterServiceOne_PositiveTest() {
    var context = new AnnotationConfigApplicationContext();
    var username = "testOne";
    var password = "0";

    context.registerBean(Properties.class, username, password);
    context.register(Service.class);
    context.refresh();

    var service = (Service) context.getBean("serviceOne");

    assertTrue(
        () -> {
          var properties = service.getProperties();

          return properties.getUsername().contains(username)
              && properties.getPassword().equals(password);
        });
    assertEquals(Service.class, service.getClass());

    context.close();
  }

  @Test
  void main_mustRegisterServiceTwo_PositiveTest() {
    var context = new AnnotationConfigApplicationContext();
    var username = "testOne";
    var password = "0";

    context.registerBean(Properties.class, username, password);
    context.register(InstanceFactory.class);
    context.refresh();

    var service = (Service) context.getBean("serviceTwo");

    assertTrue(
        () -> {
          var properties = service.getProperties();

          return properties.getUsername().contains(username)
              && properties.getPassword().equals(password);
        });
    assertEquals(Service.class, service.getClass());

    context.close();
  }

  @Test
  void main_mustRegisterServiceThree_PositiveTest() {
    var context = new AnnotationConfigApplicationContext();
    var username = "testOne";
    var password = "0";

    context.registerBean(Properties.class, username, password);
    context.register(StaticFactory.class);
    context.refresh();

    var service = (Service) context.getBean("serviceThree");

    assertTrue(
        () -> {
          var properties = service.getProperties();

          return properties.getUsername().contains(username)
              && properties.getPassword().equals(password);
        });
    assertEquals(Service.class, service.getClass());

    context.close();
  }
}
