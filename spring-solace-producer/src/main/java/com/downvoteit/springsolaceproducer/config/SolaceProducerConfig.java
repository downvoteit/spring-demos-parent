package com.downvoteit.springsolaceproducer.config;

import com.downvoteit.springsolacecommon.handler.ProducerHandler;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.XMLMessageProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolaceProducerConfig {
  @Bean
  public XMLMessageProducer createProducer(JCSMPSession session) throws JCSMPException {
    return session.getMessageProducer(new ProducerHandler());
  }
}
