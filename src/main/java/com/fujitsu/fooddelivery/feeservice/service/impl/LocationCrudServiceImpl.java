package com.fujitsu.fooddelivery.feeservice.service.impl;

import com.fujitsu.fooddelivery.feeservice.exception.InvalidIdentifierException;
import com.fujitsu.fooddelivery.feeservice.exception.WeatherStationNotFoundException;
import com.fujitsu.fooddelivery.feeservice.model.ExtraFee;
import com.fujitsu.fooddelivery.feeservice.model.Location;
import com.fujitsu.fooddelivery.feeservice.model.RegionalBaseFee;
import com.fujitsu.fooddelivery.feeservice.model.WeatherStation;
import com.fujitsu.fooddelivery.feeservice.model.repository.LocationRepository;
import com.fujitsu.fooddelivery.feeservice.model.repository.WeatherStationRepository;
import com.fujitsu.fooddelivery.feeservice.service.LocationCrudService;
import com.fujitsu.fooddelivery.feeservice.service.WeatherStationQueryService;
import jakarta.validation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation class for LocationCrudService
 */
@Component(value = "locationCrudService")
public class LocationCrudServiceImpl implements LocationCrudService {
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private WeatherStationQueryService weatherStationQueryService;
    private final Validator validator;

    public LocationCrudServiceImpl() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Override
    public Location saveLocation(Location location) throws WeatherStationNotFoundException {
        /* Weather station can be specified via id, name and wmo code */
        if (location.getWeatherStation().getId() != null) {
            WeatherStation station;
            if ((station = weatherStationQueryService.findById(location.getWeatherStation().getId())) == null)
                throw new WeatherStationNotFoundException("Weather station with id " + location.getWeatherStation().getId() + " does not exist");
            location.setWeatherStation(station);
        }
        else if (location.getWeatherStation().getName() != null) {
            WeatherStation station;
            if ((station = weatherStationQueryService.findByName(location.getWeatherStation().getName())) == null)
                throw new WeatherStationNotFoundException("Weather station with name " + location.getWeatherStation().getName() + " does not exist");
            location.setWeatherStation(station);
        }
        else if (location.getWeatherStation().getWmoCode() != null) {
            WeatherStation station;
            if ((station = weatherStationQueryService.findByWmoCode(location.getWeatherStation().getWmoCode())) == null)
                throw new WeatherStationNotFoundException("Weather station with wmo code " + location.getWeatherStation().getWmoCode() + " does not exist");
            location.setWeatherStation(station);
        }
        return locationRepository.save(location);
    }

    @Override
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    @Override
    public Location getLocationById(Integer id) { return locationRepository.findById(id).orElse(null); }

    @Override
    public Location updateLocation(Location location, Integer id) throws InvalidIdentifierException, ConstraintViolationException {
        Optional<Location> dbOptLocation = locationRepository.findById(id);
        if (dbOptLocation.isEmpty())
            throw new InvalidIdentifierException("Invalid update identifier '" + id + "'");

        Location dbLocation = dbOptLocation.get();

        // check if regional base fee update should be performed
        if (location.getRegionalBaseFee() != null) {
            Set<ConstraintViolation<RegionalBaseFee>> rbfViolations = validator.validate(location.getRegionalBaseFee());
            if (!rbfViolations.isEmpty()) {
                throw new ConstraintViolationException(rbfViolations);
            }
            dbLocation.setRegionalBaseFee(location.getRegionalBaseFee());
        }

        // update extra fee rules if requested
        if (location.getExtraFees() != null) {
            Set<ConstraintViolation<ExtraFee>> extraFeeViolations = new HashSet<>();
            for (ExtraFee extraFee : location.getExtraFees()) {
                var violations = validator.validate(extraFee);
                if (!violations.isEmpty())
                    extraFeeViolations.addAll(violations);
            }

            // check if an exception should be thrown
            if (!extraFeeViolations.isEmpty())
                throw new ConstraintViolationException(extraFeeViolations);

            dbLocation.setExtraFees(location.getExtraFees());
        }

        // weather stations can be updated by providing either local database id, name or wmo code
        if (location.getWeatherStation() != null) {
            WeatherStation station = null;
            if (location.getWeatherStation().getName() != null && (station = weatherStationQueryService.findByName(location.getWeatherStation().getName())) == null)
                throw new InvalidIdentifierException("Invalid weather station name identifier '" + location.getWeatherStation().getName() + "'");
            else if (station == null && location.getWeatherStation().getId() != null && (station = weatherStationQueryService.findById(location.getWeatherStation().getId())) == null)
                throw new InvalidIdentifierException("Invalid weather station ID " + location.getWeatherStation().getId());
            else if (station == null && location.getWeatherStation().getWmoCode() != null && (station = weatherStationQueryService.findByWmoCode(location.getWeatherStation().getWmoCode())) == null)
                throw new InvalidIdentifierException("Invalid weather station wmo code " + location.getWeatherStation().getWmoCode());

            dbLocation.setWeatherStation(station);
        }

        // location's own properties
        if (location.getCity() != null && !location.getCity().isEmpty())
            dbLocation.setCity(location.getCity());
        if (location.getCountry() != null && !location.getCountry().isEmpty())
            dbLocation.setCountry(location.getCountry());
        if (location.getCurrency() != null && !location.getCurrency().isEmpty())
            dbLocation.setCurrency(location.getCurrency());

        return locationRepository.save(dbLocation);
    }

    @Override
    public void deleteById(Integer id) throws InvalidIdentifierException {
        if (!locationRepository.existsById(id))
            throw new InvalidIdentifierException("Cannot delete location with id " + id + ", no entry is available for deletion");
        locationRepository.deleteById(id);
    }
}
