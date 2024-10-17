package com.store.infrastructure.service.impl;

import java.util.Optional;

import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.store.domain.Role;
import com.store.domain.table.User;
import com.store.dto.UserClaims;
import com.store.dto.UserRegistry;
import com.store.error.NotFoundError;
import com.store.error.NullDataError;
import com.store.error.PropertyError;
import com.store.infrastructure.persistence.UserRepository;
import com.store.infrastructure.service.UserService;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
class UserServiceImpl implements UserService {
  private UserRepository userRepository;

  @Override
  public UserClaims getByEmailAndPassword(String email, String chipherPassword) throws PropertyError {
    Optional<User> user = userRepository.findByEmail(email);

    if (user.isEmpty())
      throw new NotFoundError("Couldnt find an user with email " + email);

    if (!user.get().getPassword().equals(chipherPassword))
      throw new PropertyError("Bad credentials", 401, "Unauthorized");

    return parse(user.get());
  }

  @Override
  public UserClaims register(UserRegistry user) throws PropertyError {
    validate(user);

    UserClaims res = null;

    try {
      User saved = userRepository.save(
        new User(null,
         user.getEmail(),
         user.getFirstName(),
         user.getLastName(), 
         user.getAge(), 
         user.getPassword(), 
         Role.USER));
      
      res = parse(saved);
    } catch (DataIntegrityViolationException e) {
      isDuplicateNameViolation(e);
    }
    
    return res;
  }

  private void isDuplicateNameViolation(DataIntegrityViolationException e) throws PropertyError {
    Throwable rootCause = e.getCause();
    if (!(rootCause instanceof ConstraintViolationException)) {
      throw e;
    }
    String sqlState = ((org.hibernate.exception.ConstraintViolationException) rootCause).getSQLState();
    if ("23505".equals(sqlState)) {
      throw PropertyError.badRequest("Already exists an user with this email", e);
    }
    throw e;
  }

  UserClaims parse(User user) {
    return new UserClaims(user.getUserId(), user.getEmail(), user.getLastName(), user.getRole());
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
}
