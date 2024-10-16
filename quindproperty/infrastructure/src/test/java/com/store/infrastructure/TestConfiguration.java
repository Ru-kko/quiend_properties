package com.store.infrastructure;

import javax.sql.DataSource;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan(basePackages = "com.store.domain.table") 
@EnableJpaRepositories(basePackages = "com.store.infrastructure.persistence")
@ComponentScan(basePackages = {"com.store.infrastructure", "com.store.infrastructure.service.impl"}) 
public class TestConfiguration { 

  @Bean
  JdbcTemplate templateJdbc(DataSource datasource) {
    return new JdbcTemplate(datasource);
  }
}
