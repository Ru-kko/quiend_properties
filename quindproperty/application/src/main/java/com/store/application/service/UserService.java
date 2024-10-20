package com.store.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.store.application.port.in.UserUseCase;
import com.store.application.port.out.PasswordEncoder;
import com.store.application.port.out.UserRepository;
import com.store.domain.Role;
import com.store.domain.dto.UserClaims;
import com.store.domain.dto.UserRegistry;
import com.store.domain.error.NotFoundError;
import com.store.domain.error.NullDataError;
import com.store.domain.error.PropertyError;
import com.store.domain.table.User;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
class UserService implements UserUseCase {
  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;

  @Override
  public UserClaims findById(UUID id) throws PropertyError {
    var res = userRepository.findById(id);

    if (res.isPresent())
      return parse(res.get());

    throw new NotFoundError("Dont found an user with id " + id.toString());
  }

  @Override
  public UserClaims getByEmailAndPassword(String email, String chipherPassword) throws PropertyError {
    var user = this.findByEmail(email);

    if (Boolean.FALSE.equals(passwordEncoder.matches(user.getPassword(), chipherPassword)))
      throw new PropertyError("Bad credentials", 401, "Unauthorized");

    return parse(user);
  }

  @Override
  public UserClaims register(UserRegistry user) throws PropertyError {
    validate(user);

    UserClaims res = null;

    User saved = userRepository.save(
        new User(null,
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getAge(),
            user.getPassword(),
            Role.USER));

    res = parse(saved);

    return res;
  }

  void validate(UserRegistry registry) throws PropertyError {
    final String emailRegexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    if (registry.getEmail() == null || !registry.getEmail().matches(emailRegexp))
      throw new PropertyError("Bad email", 400, "BadRequest");

    if (registry.getFirstName() == null)
      throw new NullDataError("First name must not be null");

    if (registry.getAge() == null || registry.getAge() < 1) // ? should change this use case
      throw new PropertyError("Bad age", 400, "BadRequest");
  }

  private UserClaims parse(User user) {
    return new UserClaims(user.getUserId(), user.getEmail(), user.getLastName(), user.getRole());
  }

  @Override
  public User findByEmail(String email) throws NotFoundError {
    var res = userRepository.findByEmail(email);
    if (res.isEmpty())
      throw new NotFoundError("User not found with email " + email);

    return res.get();
  }
}
