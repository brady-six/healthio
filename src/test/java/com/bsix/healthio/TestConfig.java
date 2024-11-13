package com.bsix.healthio;

import static com.bsix.healthio.MainTest.DEFAULT_JWT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@TestConfiguration
public class TestConfig {

  private static final Logger log = LoggerFactory.getLogger(TestConfig.class);

  @Bean
  JwtDecoder jwtDecoder() {
    return token -> {
      return DEFAULT_JWT;
    };
  }
}
