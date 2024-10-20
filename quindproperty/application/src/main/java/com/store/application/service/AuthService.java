package com.store.application.service;

import com.store.application.port.in.AuthUseCase;
import com.store.application.port.in.JWTUseCase;
import com.store.application.port.in.UserUseCase;
import com.store.application.port.out.PasswordEncoder;
import com.store.domain.dto.UserClaims;
import com.store.domain.dto.UserRegistry;
import com.store.domain.error.PropertyError;
import com.store.domain.security.LoginRequest;
import com.store.domain.security.TokenResponse;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService implements AuthUseCase {
  private UserUseCase userService;
  private JWTUseCase jwtService;
  private PasswordEncoder encoder;


  @Override
  public TokenResponse register(UserRegistry user) throws PropertyError {
    var chipherPassword = encoder.encode(user.getPassword());
    
    var clone = new UserRegistry(user.getEmail(), chipherPassword, user.getFirstName(), user.getLastName(), user.getAge());

    var saved = userService.register(clone);

    return jwtService.buildToken(saved);
  }

  @Override
  public TokenResponse singIn(LoginRequest credentials) throws PropertyError {
    var chipherPassword = encoder.encode(credentials.getPassword());

    var user = userService.getByEmailAndPassword(credentials.getEmail(), chipherPassword);

    return jwtService.buildToken(user);
  }

  @Override
  public UserClaims getClaims(String token) throws PropertyError {
    return jwtService.verifyToken(token);
  }
}
