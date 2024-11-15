package com.bsix.healthio;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Profile("prod")
  @Bean
  public SecurityFilterChain securityFilterChainProd(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(c -> c.anyRequest().authenticated())
        .csrf(Customizer.withDefaults())
        .oauth2Login(Customizer.withDefaults())
        .oauth2Client(Customizer.withDefaults())
        .oauth2ResourceServer(c -> c.jwt(Customizer.withDefaults()));

    return http.build();
  }

  @Profile("dev")
  @Bean
  public SecurityFilterChain securityFilterChainDev(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(c -> c.anyRequest().authenticated())
        .csrf()
        .disable()
        .oauth2Login(Customizer.withDefaults())
        .oauth2Client(Customizer.withDefaults())
        .oauth2ResourceServer(c -> c.jwt(Customizer.withDefaults()));

    return http.build();
  }
}
