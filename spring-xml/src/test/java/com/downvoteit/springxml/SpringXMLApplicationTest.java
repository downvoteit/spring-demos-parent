package com.downvoteit.springxml;

import com.downvoteit.springxml.service.ListService;
import com.downvoteit.springxml.service.Service;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SpringXMLApplicationTest {
  AbstractApplicationContext applicationContext;
  Service service;
  ListService listService;

  @BeforeEach
  void setContext() {
    applicationContext = new ClassPathXmlApplicationContext("context.xml");
  }

  @AfterEach
  void closeContext() {
    applicationContext.close();
  }

  @Nested
  class ServiceTests {
    @Test
    void main_mustRegisterService_PositiveTest() {
      service = (Service) applicationContext.getBean("service");

      assertNotNull(service);

      applicationContext.close();
    }

    @ParameterizedTest
    @CsvSource(
        value = {"0:one", "1:two", "2:three", "3:four"},
        delimiter = ':')
    void main_mustRegisterListService_PositiveTest(String index, String expectedName) {
      listService = (ListService) applicationContext.getBean("listService");

      String actualName = listService.getRepositories().get(Integer.parseInt(index)).getName();

      assertEquals(expectedName, actualName);

      applicationContext.close();
    }
  }

  @Nested
  class RepositoryTests {
    @BeforeEach
    void setService() {
      service = (Service) applicationContext.getBean("service");
    }

    @Test
    void main_mustRegisterRepositoryOne_PositiveTest() {
      assertEquals("one", service.getRepositoryOne().getName());
    }

    @Test
    void main_mustRegisterRepositoryTwo_PositiveTest() {
      assertEquals("two", service.getRepositoryTwo().getName());
    }

    @Test
    void main_mustRegisterRepositoryThree_PositiveTest() {
      assertEquals("three", service.getRepositoryThree().getName());
    }

    @Test
    void main_mustRegisterRepositoryFour_PositiveTest() {
      assertEquals("four", service.getRepositoryFour().getName());
    }
  }
}
