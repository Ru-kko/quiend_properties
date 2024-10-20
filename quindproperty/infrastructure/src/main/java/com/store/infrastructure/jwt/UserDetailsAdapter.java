package com.store.infrastructure.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.store.application.port.in.UserUseCase;
import com.store.domain.error.NotFoundError;
import com.store.domain.table.User;

import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class UserDetailsAdapter implements UserDetailsService {
  private UserUseCase userService;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    try {
      var user = userService.findByEmail(email);

      return new UserDetailsImpl(user);
    } catch (NotFoundError e) {
      throw new UsernameNotFoundException(e.getMessage());
    }
  }
  
  @AllArgsConstructor
  private static class UserDetailsImpl implements UserDetails {
    private final User usr;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      GrantedAuthority auth = () -> "ROLE_".concat(usr.getRole().toString());

      return List.of(auth);
    }

    @Override
    public String getPassword() {
      return usr.getPassword();
    }

    @Override
    public String getUsername() {
      return usr.getEmail();
    }
  }
  
}
