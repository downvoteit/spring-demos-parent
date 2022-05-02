package com.downvoteit.springsolaceproducer.config;

import com.downvoteit.springsolacecommon.util.SessionUtil;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SessionConfig {
  @Bean
  public JCSMPSession createSession(JCSMPProperties properties) throws JCSMPException {
    properties.setProperty(JCSMPProperties.IGNORE_DUPLICATE_SUBSCRIPTION_ERROR, true);

    var session = JCSMPFactory.onlyInstance().createSession(properties);

    session.connect();

    SessionUtil.checkCapabilities(session);

    return session;
  }
}
