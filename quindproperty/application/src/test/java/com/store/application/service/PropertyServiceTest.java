package com.store.application.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.store.application.ApplicationConfig;
import com.store.application.port.out.CityRepository;
import com.store.application.port.out.PropertyRepository;
import com.store.domain.dto.PropertyRegistry;
import com.store.domain.error.NullDataError;
import com.store.domain.error.PropertyError;
import com.store.domain.table.City;
import com.store.domain.table.Property;

class PropertyServiceTest {
  @Mock
  private PropertyRepository propertyRepository;
  @Mock
  private ApplicationConfig applicationConfig;
  @Mock
  private CityRepository cityRepository;
  @InjectMocks
  private PropertyService propertyService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(applicationConfig.getPageSize()).thenReturn(10);
  }

  // _______________________ PropertyService.Find() ____________________
  @Test
  void findWithoutBound() {
    int page = 0;
    Page<Property> mockPage = new PageImpl<>(Collections.emptyList(),
        PageRequest.of(page, applicationConfig.getPageSize()), 5);

    when(propertyRepository.findAllWithLowerPrice(eq(BigDecimal.ZERO), any(PageRequest.class)))
        .thenReturn(mockPage);

    var result = propertyService.find(null, null, page);

    assertEquals(mockPage.getTotalElements(), result.getTotalElements());
    Mockito.verify(propertyRepository, Mockito.times(1)).findAllWithLowerPrice(eq(BigDecimal.ZERO),
        any(PageRequest.class));
  }

  @Test
  void findWithLeftBound() {
    int page = 0;
    BigDecimal lowerBound = new BigDecimal(700000);

    Page<Property> mockPage = new PageImpl<>(Collections.emptyList(),
        PageRequest.of(page, applicationConfig.getPageSize()), 3);

    when(propertyRepository.findAllWithLowerPrice(eq(lowerBound), any(PageRequest.class)))
        .thenReturn(mockPage);

    var result = propertyService.find(lowerBound, null, page);

    assertEquals(mockPage.getTotalElements(), result.getTotalElements());

    Mockito.verify(propertyRepository, Mockito.times(1))
        .findAllWithLowerPrice(eq(lowerBound), any(PageRequest.class));
  }

  @Test
  void findWithBothBounds() {
    int page = 0;
    BigDecimal lowerBound = new BigDecimal(500000);
    BigDecimal upperBound = new BigDecimal(800000);

    Page<Property> mockPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, 10), 7);

    when(propertyRepository.findAllWithPriceRange(eq(lowerBound), eq(upperBound), any(PageRequest.class)))
        .thenReturn(mockPage);

    var result = propertyService.find(lowerBound, upperBound, page);

    assertEquals(mockPage.getTotalElements(), result.getTotalElements());

    Mockito.verify(propertyRepository, Mockito.times(1))
        .findAllWithPriceRange(eq(lowerBound), eq(upperBound), any(PageRequest.class));
  }

  // _____________________ transform _______________________________
  @Test
  void transformNullNameThrowsNullDataError() {
    PropertyRegistry dto = new PropertyRegistry();
    dto.setImage("image.jpg");
    dto.setLocation(UUID.randomUUID());
    dto.setPrice(new BigDecimal("1000000"));

    NullDataError exception = assertThrows(NullDataError.class, () -> propertyService.transform(dto));
    assertEquals("Not provided name", exception.getMessage());
  }

  @Test
  void transformNullImageThrowsNullDataError() {
    PropertyRegistry dto = new PropertyRegistry();
    dto.setName("Test Property");
    dto.setLocation(UUID.randomUUID());
    dto.setPrice(new BigDecimal("1000000"));

    NullDataError exception = assertThrows(NullDataError.class, () -> propertyService.transform(dto));
    assertEquals("Not provided image", exception.getMessage());
  }

  @Test
  void transformNullLocationThrowsNullDataError() {
    PropertyRegistry dto = new PropertyRegistry();
    dto.setName("Test Property");
    dto.setImage("image.jpg");
    dto.setPrice(new BigDecimal("1000000"));

    NullDataError exception = assertThrows(NullDataError.class, () -> propertyService.transform(dto));
    assertEquals("Not provided valid location id", exception.getMessage());
  }

  @Test
  void transformNonExistentCityThrowsPropertyError() {
    PropertyRegistry dto = new PropertyRegistry();
    dto.setName("Test Property");
    dto.setImage("image.jpg");
    dto.setLocation(UUID.randomUUID());
    dto.setPrice(new BigDecimal("1000000"));

    when(cityRepository.findById(dto.getLocation())).thenReturn(Optional.empty());

    PropertyError exception = assertThrows(PropertyError.class, () -> propertyService.transform(dto));
    assertEquals("Not found city with id " + dto.getLocation().toString(), exception.getMessage());
  }

  @Test
  void transformLowPriceInBogotaThrowsPropertyError() {
    final UUID bogota = UUID.fromString("a4b2c9d7-258e-4f2f-a1ad-1c7f5f2a9d75");
    PropertyRegistry dto = new PropertyRegistry();
    dto.setName("Test Property");
    dto.setImage("image.jpg");
    dto.setLocation(bogota); // Bogota
    dto.setPrice(new BigDecimal("1000000"));

    when(cityRepository.findById(dto.getLocation())).thenReturn(Optional.of(new City(bogota, "Bogota")));

    PropertyError exception = assertThrows(PropertyError.class, () -> propertyService.transform(dto));
    assertEquals("Price must be > 2'000.000 in this city", exception.getMessage());
  }

  @Test
  void testTransformValidInputReturnsProperty() throws PropertyError {
    final UUID bogota = UUID.fromString("a4b2c9d7-258e-4f2f-a1ad-1c7f5f2a9d75");
    PropertyRegistry dto = new PropertyRegistry();
    dto.setName("Test Property");
    dto.setImage("image.jpg");
    dto.setLocation(UUID.fromString("a4b2c9d7-258e-4f2f-a1ad-1c7f5f2a9d75")); // Bogota
    dto.setPrice(new BigDecimal("2000001"));

    when(cityRepository.findById(dto.getLocation())).thenReturn(Optional.of(new City(bogota, "Bogota")));

    Property result = propertyService.transform(dto);
    assertNotNull(result);
    assertEquals(dto.getName(), result.getName());
    assertEquals(dto.getImage(), result.getImg());
    assertEquals(dto.getPrice(), result.getPrice());
    assertEquals("Bogota", result.getLocation().getName());
  }

  // ____________________ Save ___________________________________
  @Test
  void testSaveValidProperty() throws PropertyError {
    PropertyRegistry newProperty = new PropertyRegistry("New Appartment In Bogota",
        UUID.fromString("a4b2c9d7-258e-4f2f-a1ad-1c7f5f2a9d75"), "image.png", new BigDecimal("2500000.00"));

    City mockCity = new City(newProperty.getLocation(), "Medellin");

    when(cityRepository.findById(newProperty.getLocation())).thenReturn(Optional.of(mockCity));

    Property savedMockProperty = new Property();
    savedMockProperty.setPropertyId(UUID.randomUUID());
    savedMockProperty.setName(newProperty.getName());
    savedMockProperty.setLocation(mockCity);
    savedMockProperty.setImg(newProperty.getImage());
    savedMockProperty.setPrice(newProperty.getPrice());
    savedMockProperty.setActive(true);
    savedMockProperty.setAvailable(true);

    when(propertyRepository.save(any(Property.class))).thenReturn(savedMockProperty);

    Property savedProperty = assertDoesNotThrow(() -> propertyService.save(newProperty));

    UUID newPropertyId = savedProperty.getPropertyId();
    assertNotNull(newPropertyId);

    assertEquals(savedMockProperty.getActive(), savedProperty.getActive());
    assertEquals(savedMockProperty.getLocation().getCityId(), savedProperty.getLocation().getCityId());
    assertEquals(savedMockProperty.getImg(), savedProperty.getImg());
    assertEquals(savedMockProperty.getName(), savedProperty.getName());
    assertEquals(savedMockProperty.getPrice(), savedProperty.getPrice());

    Mockito.verify(cityRepository, Mockito.times(1)).findById(newProperty.getLocation());
    Mockito.verify(propertyRepository, Mockito.times(1)).save(any(Property.class));
  }

  // ____________________ Update ________________________________
  @Test
  void testUpdateNotFound() {
    UUID nonExistentId = UUID.randomUUID();
    PropertyRegistry newData = new PropertyRegistry();

    when(propertyRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    PropertyError exception = assertThrows(PropertyError.class, () -> propertyService.update(nonExistentId, newData));

    assertEquals(404, exception.getCode());
  }

  @Test
  void testUpdatePropertyRentedChangePrice() {
    UUID rentedPropertyId = UUID.fromString("3a9f2dab-ae3a-437f-aeaa-f0b88a60a3ee");
    PropertyRegistry newData = new PropertyRegistry();
    newData.setPrice(new BigDecimal(1100000));

    Property rentedProperty = new Property();
    rentedProperty.setPropertyId(rentedPropertyId);
    rentedProperty.setAvailable(false);

    when(propertyRepository.findById(rentedPropertyId)).thenReturn(Optional.of(rentedProperty));

    PropertyError exception = assertThrows(PropertyError.class,
        () -> propertyService.update(rentedPropertyId, newData));
    assertEquals(400, exception.getCode());
  }

  @Test
  void testUpdatePropertyRentedChangeLocation() {
    UUID rentedPropertyId = UUID.fromString("3a9f2dab-ae3a-437f-aeaa-f0b88a60a3ee");
    PropertyRegistry newData = new PropertyRegistry();

    newData.setLocation(UUID.fromString("a4b2c9d7-258e-4f2f-a1ad-1c7f5f2a9d75"));

    City mockCity = new City(newData.getLocation(), "Medellin");
    when(cityRepository.findById(newData.getLocation())).thenReturn(Optional.of(mockCity));

    Property rentedProperty = new Property();
    rentedProperty.setAvailable(false);
    rentedProperty.setLocation(new City(rentedPropertyId, null));
    when(propertyRepository.findById(rentedPropertyId)).thenReturn(Optional.of(rentedProperty));

    PropertyError exception = assertThrows(PropertyError.class,
        () -> propertyService.update(rentedPropertyId, newData));
    assertEquals(400, exception.getCode());
  }

  @Test
  void testUpdateAvailablePropertyFullData() throws PropertyError {
    City cali = new City(UUID.fromString("c21d6f5e-7b58-4d81-9fc8-91e7c69d6e9a"), "Cali");
    UUID availablePropertyId = UUID.fromString("48a234c4-ef02-4f96-8a04-82307b1d31a4"); // Luxury Apartment Medellin
    PropertyRegistry newData = new PropertyRegistry();
    newData.setName("Updated Apartment Medellin");
    newData.setPrice(new BigDecimal("3500000.00"));
    newData.setLocation(cali.getCityId());
    newData.setImage("newimage.jpg");

    Property originalProperty = new Property();
    originalProperty.setPropertyId(availablePropertyId);
    originalProperty.setAvailable(true);
    originalProperty.setActive(true);
    originalProperty.setName("Old Apartment Medellin");
    originalProperty.setPrice(new BigDecimal("3000000.00"));
    originalProperty.setLocation(new City());
    originalProperty.setImg("oldimage.jpg");

    when(propertyRepository.findById(availablePropertyId)).thenReturn(Optional.of(originalProperty));
    when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(cityRepository.findById(cali.getCityId())).thenReturn(Optional.of(cali));

    Property updatedProperty = assertDoesNotThrow(() -> propertyService.update(availablePropertyId, newData));
    assertNotNull(updatedProperty);
    assertEquals(availablePropertyId, updatedProperty.getPropertyId());

    assertEquals(newData.getName(), updatedProperty.getName());
    assertEquals(newData.getPrice(), updatedProperty.getPrice());
    assertEquals(newData.getLocation(), updatedProperty.getLocation().getCityId());
    assertEquals(newData.getImage(), updatedProperty.getImg());
  }

  @Test
  void testUpdateAvailablePropertyPartialData() throws PropertyError {
    UUID availablePropertyId = UUID.fromString("48a234c4-ef02-4f96-8a04-82307b1d31a4"); // Luxury Apartment Medellin
    PropertyRegistry newData = new PropertyRegistry();
    newData.setName("Updated Apartment Medellin");

    Property originalProperty = new Property();
    originalProperty.setPropertyId(availablePropertyId);
    originalProperty.setAvailable(true);
    originalProperty.setActive(true);
    originalProperty.setName("Old Apartment Medellin");
    originalProperty.setPrice(new BigDecimal("3000000.00"));
    originalProperty.setLocation(new City(UUID.randomUUID(), "rand"));
    originalProperty.setImg("oldimage.jpg");

    when(propertyRepository.findById(availablePropertyId)).thenReturn(Optional.of(originalProperty));
    when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(cityRepository.findById(originalProperty.getLocation().getCityId()))
        .thenReturn(Optional.of(originalProperty.getLocation()));

    Property updatedProperty = assertDoesNotThrow(() -> propertyService.update(availablePropertyId, newData));
    assertNotNull(updatedProperty);

    assertEquals(newData.getName(), updatedProperty.getName());
    assertEquals(originalProperty.getPrice(), updatedProperty.getPrice());
    assertEquals(originalProperty.getLocation().getCityId(), updatedProperty.getLocation().getCityId());
    assertEquals(originalProperty.getImg(), updatedProperty.getImg());
    assertEquals(originalProperty.getActive(), updatedProperty.getActive());
    assertEquals(originalProperty.getAvailable(), updatedProperty.getAvailable());
  }

  // ___________________ Toggle Availability ________________________
  @Test
  void testToggleAvailabilityPropertyFound() {
    UUID propertyId = UUID.fromString("48a234c4-ef02-4f96-8a04-82307b1d31a4");

    Property originalProperty = new Property();
    originalProperty.setPropertyId(propertyId);
    originalProperty.setAvailable(true);
    originalProperty.setActive(true);

    when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(originalProperty));

    Property toggledProperty = assertDoesNotThrow(() -> propertyService.toggleAvailability(propertyId));

    assertNotNull(toggledProperty);
    assertFalse(toggledProperty.getAvailable());

  }

  @Test
  void testToggleAvailabilityPropertyNotFound() {
    UUID nonExistentId = UUID.randomUUID();

    when(propertyRepository.findById(nonExistentId)).thenReturn(Optional.empty());
    PropertyError exception = assertThrows(PropertyError.class,
        () -> propertyService.toggleAvailability(nonExistentId));

    assertEquals(404, exception.getCode());
  }

  @Test
  void testToggleAvailabilityPropertyInactive() {
    UUID inactivePropertyId = UUID.fromString("c8447bdf-559f-465b-9333-2c2dc38addbf");

    Property inactiveProperty = new Property();
    inactiveProperty.setPropertyId(inactivePropertyId);
    inactiveProperty.setActive(false);

    when(propertyRepository.findById(inactivePropertyId)).thenReturn(Optional.of(inactiveProperty));

    PropertyError exception = assertThrows(PropertyError.class,
        () -> propertyService.toggleAvailability(inactivePropertyId));

    assertEquals(404, exception.getCode());
  }

  // _________________ Delete __________________________________
  @Test
  void testDeleteOldProperty() {
    UUID oldPropertyId = UUID.fromString("48a234c4-ef02-4f96-8a04-82307b1d31a4"); // Luxury Apartment Medellin

    Property originalProperty = new Property();
    originalProperty.setPropertyId(oldPropertyId);
    originalProperty.setAvailable(false);
    originalProperty.setActive(true);

    when(propertyRepository.findById(oldPropertyId)).thenReturn(Optional.of(originalProperty));

    PropertyError exception = assertThrows(PropertyError.class, () -> propertyService.delete(oldPropertyId));

    assertEquals(400, exception.getCode());
  }
}
