package com.downvoteit.springxml;

import com.downvoteit.springxml.service.ListService;
import com.downvoteit.springxml.service.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Slf4j
public class SpringXMLApplication {
  public static void main(String[] args) {
    var applicationContext = new ClassPathXmlApplicationContext("context.xml");

    var service = (Service) applicationContext.getBean("service");
    var listService = (ListService) applicationContext.getBean("listService");

    log.info("{}", service);
    log.info("{}", listService);
  }
}
