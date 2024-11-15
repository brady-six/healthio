package com.bsix.healthio;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile({"dev", "test"})
public class TestSecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(req -> req.anyRequest().authenticated())
        .csrf()
        .disable()
        .oauth2Client(Customizer.withDefaults())
        .oauth2Login(Customizer.withDefaults())
        .oauth2ResourceServer(c -> c.jwt(Customizer.withDefaults()));
    return http.build();
  }
}
