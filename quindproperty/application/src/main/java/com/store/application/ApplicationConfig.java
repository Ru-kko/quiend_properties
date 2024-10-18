package com.store.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
@ConfigurationProperties(prefix = "app")
public class ApplicationConfig {
  @Value("${app.security.jwt.secret}")
  private String jwtSecret;
  @Value("${app.security.jwt.expires}")
  private Long expiration;
  @Value("${app.property.page-size}")
  private Integer pageSize;
  @Value("${app.property.time2delete:2592000000}")
  private Long time2DeleteProperty; // Default 30 days
}
