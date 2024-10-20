package com.store.infrastructure.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class JWTConfiguration {
  private JWTAuthenticationFilter authenticationFilter;
  private UserDetailsService userDetailsService;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http)
      throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.disable()) // !
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.POST, "/session/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/properties").permitAll()
            .anyRequest().hasRole("ADMIN"))
        .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .userDetailsService(userDetailsService);

    return http.build();
  }
}
