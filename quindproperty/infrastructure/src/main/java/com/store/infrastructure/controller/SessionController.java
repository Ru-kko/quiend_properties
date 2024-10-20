package com.store.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.store.application.port.in.AuthUseCase;
import com.store.domain.dto.UserRegistry;
import com.store.domain.error.PropertyError;
import com.store.domain.security.LoginRequest;
import com.store.domain.security.TokenResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("session")
@RequiredArgsConstructor
public class SessionController {
  private final AuthUseCase authService;

  @PostMapping("signup")
  public TokenResponse postMethodName(@RequestBody UserRegistry newUser) {
    try {
      return authService.register(newUser);
    } catch (Exception e) {
      handleError(e);
      return null;
    }
  }

  @PostMapping("signin")
  public TokenResponse postMethodName(@RequestBody LoginRequest req) {
    try {
      return authService.singIn(req);
    } catch (Exception e) {
      handleError(e);
      return null;
    }
  }
  

  private void handleError(Throwable e) throws ResponseStatusException {
    if (e instanceof PropertyError) {
      PropertyError pError = (PropertyError) e;
      log.error(e.getMessage());
      throw new ResponseStatusException(HttpStatusCode.valueOf(pError.getCode()), pError.getMessage(), e);
    }

    log.error(e.getMessage(), e);
    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "We are in a trouble, try again later");
  }
}
