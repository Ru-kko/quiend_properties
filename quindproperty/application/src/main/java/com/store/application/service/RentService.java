package com.store.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.store.application.port.in.RentUseCase;
import com.store.application.port.out.PropertyRepository;
import com.store.application.port.out.RentRepository;
import com.store.application.port.out.UserRepository;
import com.store.domain.dto.CleanProperty;
import com.store.domain.dto.CleanRent;
import com.store.domain.dto.UserClaims;
import com.store.domain.error.NotFoundError;
import com.store.domain.error.PropertyError;
import com.store.domain.table.Rent;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
class RentService implements RentUseCase {
  private RentRepository rentRepository;
  private UserRepository userRepository;
  private PropertyRepository propertyRepository;
  private PropertyService propertyService;

  @Override
  public CleanRent rentProperty(UserClaims user, UUID property) throws PropertyError {
    var userOpt = userRepository.findById(user.getUserId());
    if (userOpt.isEmpty())
      throw new NotFoundError("Not found an user with id " + user.getUserId());
    
    var propertyOpt = propertyRepository.findById(property);
    if (propertyOpt.isEmpty() || Boolean.FALSE.equals(propertyOpt.get().getAvailable() && propertyOpt.get().getActive()))
      throw new NotFoundError("Not found a property with id " + user.getUserId());

    propertyService.toggleAvailability(property);
    var toSave = new Rent();

    toSave.setUser(userOpt.get());
    toSave.setProperty(propertyOpt.get());

    var res = rentRepository.save(toSave);


    return parseRent(res);
  }

  CleanRent parseRent(Rent rent) {
    var res = new CleanRent();
    var cleanProperty = new CleanProperty();
    var cleanUser = new UserClaims();

    cleanProperty.setPrice(rent.getProperty().getPrice());
    cleanProperty.setImage(rent.getProperty().getImg());
    cleanProperty.setLocation(rent.getProperty().getLocation());
    cleanProperty.setPropertyId(rent.getProperty().getPropertyId());

    cleanUser.setEmail(rent.getUser().getEmail());
    cleanUser.setLastName(rent.getUser().getLastName());
    cleanUser.setRole(rent.getUser().getRole());
    cleanUser.setUserId(rent.getUser().getUserId());

    res.setRentID(rent.getRentId());
    res.setProperty(cleanProperty);
    res.setUser(cleanUser);
    res.setRentDate(rent.getRentDate());
    
    return res;
  }
}

