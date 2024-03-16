package com.fujitsu.fooddelivery.feeservice.unit;

import com.fujitsu.fooddelivery.feeservice.exception.InvalidIdentifierException;
import com.fujitsu.fooddelivery.feeservice.exception.WeatherStationNotFoundException;
import com.fujitsu.fooddelivery.feeservice.model.*;
import com.fujitsu.fooddelivery.feeservice.model.repository.LocationRepository;
import com.fujitsu.fooddelivery.feeservice.service.WeatherStationQueryService;
import com.fujitsu.fooddelivery.feeservice.service.impl.LocationCrudServiceImpl;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.when;

@ExtendWith(MockitoExtension.class)
public class LocationCrudServiceTests {
    @Mock
    private LocationRepository locationRepository;

    @Mock
    private WeatherStationQueryService weatherStationQueryService;

    @InjectMocks
    private LocationCrudServiceImpl service;

    private List<Location> locations;

    @BeforeEach
    public void setup() {
        locations = new ArrayList<>();
        locations.add(Location.builder()
            .id(1)
            .country("Estonia")
            .city("Tallinn")
            .currency("EUR")
            .regionalBaseFee(RegionalBaseFee.builder()
                .id(1)
                .car(new BigDecimal("4.00"))
                .scooter(new BigDecimal("3.50"))
                .bike(new BigDecimal("3.00"))
                .build()
            )
            .weatherStation(WeatherStation.builder()
                .id(1)
                .name("Tallinn-Harku")
                .wmoCode(26038)
                .longitude(24.602891666624284)
                .latitude(59.398122222355134)
                .build()
            )
            .build()
        );

        locations.add(Location.builder()
            .id(2)
            .country("Estonia")
            .city("Tartu")
            .currency("EUR")
            .regionalBaseFee(RegionalBaseFee.builder()
                .id(2)
                .car(new BigDecimal("3.50"))
                .scooter(new BigDecimal("3.00"))
                .bike(new BigDecimal("2.50"))
                .build()
            )
            .weatherStation(WeatherStation.builder()
                .id(2)
                .name("Tartu-Tõravere")
                .wmoCode(26242)
                .longitude(26.46130555576748)
                .latitude(58.264072222179834)
                .build()
            )
            .build()
        );
    }

    @Test
    @DisplayName("LocationCrudService.saveLocation method should return a correct Location object")
    public void testSaveLocation() throws IOException {
        given(weatherStationQueryService.findByName("Tartu-Tõravere"))
            .willReturn(locations.get(1).getWeatherStation());
        when(locationRepository.save(any(Location.class)))
            .thenAnswer(i -> i.getArguments()[0]);

        Location tartu = Location.builder()
            .country(locations.get(1).getCountry())
            .city(locations.get(1).getCity())
            .currency(locations.get(1).getCurrency())
            .regionalBaseFee(locations.get(1).getRegionalBaseFee())
            .weatherStation(WeatherStation.builder().name("Tartu-Tõravere").build())
            .build();

        try {
            Location location = service.saveLocation(tartu);
            location.setId(locations.get(1).getId());
            assertEquals(locations.get(1), location);
        }
        catch (WeatherStationNotFoundException e) {
            fail();
        }
    }

    @Test
    @DisplayName("LocationCrudService.getAllLocations should return a list of valid Location objects")
    public void testGetAllLocations() {
        given(locationRepository.findAll())
            .willReturn(locations);

        assertEquals(locations, service.getAllLocations());
    }

    @Test
    @DisplayName("LocationCrudService.getLocationById should return a valid Location object when provided ID is correct")
    public void testGetLocationById_ValidId_ExpectLocation() {
        given(locationRepository.findById(locations.get(0).getId()))
            .willReturn(Optional.of(locations.get(0)));
        given(locationRepository.findById(locations.get(1).getId()))
            .willReturn(Optional.of(locations.get(1)));

        assertEquals(locations.get(0), service.getLocationById(locations.get(0).getId()));
        assertEquals(locations.get(1), service.getLocationById(locations.get(1).getId()));
    }

    @Test
    @DisplayName("LocationCrudService.getLocationById should return null with invalid provided ID")
    public void testGetLocationById_InvalidId_ExpectNull() {
        when(locationRepository.findById(anyInt()))
            .thenReturn(Optional.empty());

        assertNull(service.getLocationById(1001));
    }

    @Test
    @DisplayName("LocationCrudService.updateLocation should throw InvalidIdentifierException when provided ID is invalid")
    public void testUpdateLocation_InvalidId_ExpectInvalidIdentifierException() {
        when(locationRepository.findById(anyInt()))
            .thenReturn(Optional.empty());

        Location update = Location.builder().city("Valga").build();
        assertThrows(InvalidIdentifierException.class, () -> service.updateLocation(update, 1001));
    }

    @Test
    @DisplayName("LocationCrudService.updateLocation should throw ConstraintViolationException when regionalBaseFee constraint check fails")
    public void testUpdateLocation_RegionalBaseFeeConstraintViolation_ExpectConstraintViolationException() {
        when(locationRepository.findById(locations.get(0).getId()))
                .thenReturn(Optional.of(locations.get(0)));

        Location update = Location.builder()
                .regionalBaseFee(new RegionalBaseFee())
                .build();
        assertThrows(ConstraintViolationException.class, () -> service.updateLocation(update, locations.get(0).getId()));
    }

    @Test
    @DisplayName("LocationCrudService.updateLocation should throw ConstraintViolationException when extraFees constraint check fails")
    public void testUpdateLocation_ExtraFeeConstraintViolation_ExpectConstraintViolationException() {
        when(locationRepository.findById(locations.get(0).getId()))
            .thenReturn(Optional.of(locations.get(0)));

        Location update = Location.builder()
            .extraFees(List.of(new AirTemperatureExtraFee()))
            .build();
        assertThrows(ConstraintViolationException.class, () -> service.updateLocation(update, locations.get(0).getId()));
    }

    @Test
    @DisplayName("LocationCrudService.updateLocation should throw InvalidIdentifierException when weatherStation identifiers (id, name, wmo) are invalid")
    public void testUpdateLocation_WeatherStationInvalidIdentifiers_ExpectInvalidIdentifierException() {
        when(locationRepository.findById(locations.get(0).getId()))
            .thenReturn(Optional.of(locations.get(0)));

        when(weatherStationQueryService.findById(anyInt()))
            .thenReturn(null);
        when(weatherStationQueryService.findByName(anyString()))
            .thenReturn(null);
        when(weatherStationQueryService.findByWmoCode(anyInt()))
            .thenReturn(null);

        Location updateByName = Location.builder().weatherStation(WeatherStation.builder().name("Tallinn").build()).build();
        Location updateById = Location.builder().weatherStation(WeatherStation.builder().id(1001).build()).build();
        Location updateByWmoCode = Location.builder().weatherStation(WeatherStation.builder().wmoCode(1337).build()).build();

        assertThrows(InvalidIdentifierException.class, () -> service.updateLocation(updateByName, locations.get(0).getId()));
        assertThrows(InvalidIdentifierException.class, () -> service.updateLocation(updateById, locations.get(0).getId()));
        assertThrows(InvalidIdentifierException.class, () -> service.updateLocation(updateByWmoCode, locations.get(0).getId()));
    }

    @Test
    @DisplayName("LocationCrudService.updateLocation should return updated Location object")
    public void testUpdateLocation_ValidRequest_ExpectUpdatedLocationObject() {
        when(locationRepository.findById(locations.get(0).getId()))
            .thenReturn(Optional.of(locations.get(0)));
        when(locationRepository.save(any()))
            .thenReturn(locations.get(0));

        Location update = Location.builder()
            .city("Maardu")
            .regionalBaseFee(RegionalBaseFee.builder()
                .car(new BigDecimal("4.5"))
                .scooter(new BigDecimal("4.0"))
                .bike(new BigDecimal("3.5"))
                .build()
            )
            .extraFees(List.of(new AirTemperatureExtraFee(
                new BigDecimal("1.0"),
                VehicleRule.NOT_APPLICABLE,
                VehicleRule.APPLICABLE,
                VehicleRule.APPLICABLE,
                null,
                -10.f
            )))
            .build();

        Location expected = new Location();
        expected.setId(locations.get(0).getId());
        expected.setCountry(locations.get(0).getCountry());
        expected.setCurrency(locations.get(0).getCurrency());
        expected.setCity(update.getCity());
        expected.setRegionalBaseFee(update.getRegionalBaseFee());
        expected.setExtraFees(update.getExtraFees());
        expected.setWeatherStation(locations.get(0).getWeatherStation());

        try {
            Location actual = service.updateLocation(update, locations.get(0).getId());
            assertEquals(expected, actual);
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("LocationCrudService.deleteById should throw InvalidIdentifierException when provided ID is invalid")
    public void testDeleteById_InvalidIdentifier_ExpectInvalidIdentifierException() {
        assertThrows(InvalidIdentifierException.class, () -> service.deleteById(1001));
    }
}
