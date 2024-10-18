package com.store.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.store.application.ApplicationConfig;
import com.store.application.port.out.PasswordEncoder;

@Configuration
@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties(ApplicationConfig.class)
@EntityScan(basePackages = "com.store.domain.table")
@ComponentScan(basePackages = {"com.store.application", "com.store.infrastructure.persistnce"}) 
public class StoreEntryPoint {
  public static void main(String[] args) {
    SpringApplication.run(StoreEntryPoint.class, args);
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    var encoder = new BCryptPasswordEncoder();
    return encoder::encode;
  }
}
