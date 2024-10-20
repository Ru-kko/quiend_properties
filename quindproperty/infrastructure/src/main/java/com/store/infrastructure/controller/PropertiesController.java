package com.store.infrastructure.controller;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.store.application.port.in.PropertyUseCase;
import com.store.application.port.in.RentUseCase;
import com.store.domain.dto.CleanRent;
import com.store.domain.dto.PropertyRegistry;
import com.store.domain.dto.UserClaims;
import com.store.domain.error.PropertyError;
import com.store.domain.table.Property;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@Slf4j
@RestController
@RequestMapping("properties")
@RequiredArgsConstructor
public class PropertiesController {
  private final PropertyUseCase propertyService;
  private final RentUseCase rentService;

  @GetMapping
  public Page<Property> fillProperties(@RequestParam(required = false) BigDecimal lower,
      @RequestParam(required = false) BigDecimal upper,
      @RequestParam(required = false, defaultValue = "0") Integer page) {
    if (lower != null && upper != null && lower.compareTo(upper) > 1) {
      var error = new PropertyError("lower > upper", 400, "BadRequest");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error.getMessage(), error);
    }

    try {
      return propertyService.find(lower, upper, page);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "We are in a trouble, try again later");
    }
  }

  @DeleteMapping("{id}")
  public String deleteProperty(@PathVariable("id") UUID id) {
    try {
      propertyService.delete(id);
      return "Property deleted succesfuly"; 
    } catch (Exception e) {
      handleError(e);
      return null;
    }
  }

  @PostMapping
  public Property postMethodName(@RequestBody PropertyRegistry newProperty) {
    try {
      return propertyService.save(newProperty);
    } catch (PropertyError e) {
      handleError(e);
      return null;
    }
  }

  @PutMapping("{id}")
  public Property putMethodName(@PathVariable UUID id, @RequestBody PropertyRegistry changes) {
    try {
      return propertyService.update(id, changes);
    } catch (PropertyError e) {
      handleError(e);
      return null;
    }    
  }
  @PostMapping("rent/{id}")
  public CleanRent postMethodName(@PathVariable UUID id, Authentication auth) {
    try {
      var userData = (UserClaims) auth.getPrincipal();
      return rentService.rentProperty(userData, id);
    } catch (PropertyError e) {
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

