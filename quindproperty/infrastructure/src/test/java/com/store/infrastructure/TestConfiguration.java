package com.store.infrastructure;

import javax.sql.DataSource;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.store.application.ApplicationConfig;
import com.store.application.port.out.PasswordEncoder;

@Configuration
@SpringBootConfiguration
@EnableAutoConfiguration
@EnableConfigurationProperties(ApplicationConfig.class)
@EntityScan(basePackages = "com.store.domain.table")
@ComponentScan(basePackages = {"com.store.application", "com.store.infrastructure.persistnce"}) 
public class TestConfiguration { 

  @Bean
  JdbcTemplate templateJdbc(DataSource datasource) {
    return new JdbcTemplate(datasource);
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return p -> p.concat("encode");
  }
}
