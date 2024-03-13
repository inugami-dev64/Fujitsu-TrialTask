package com.fujitsu.fooddelivery.feeservice.controller;

import com.fujitsu.fooddelivery.feeservice.exception.ForbiddenVehicleException;
import com.fujitsu.fooddelivery.feeservice.model.Location;
import com.fujitsu.fooddelivery.feeservice.model.VehicleType;
import com.fujitsu.fooddelivery.feeservice.model.WeatherObservation;
import com.fujitsu.fooddelivery.feeservice.model.repository.LocationRepository;
import com.fujitsu.fooddelivery.feeservice.model.repository.WeatherObservationRepository;
import com.fujitsu.fooddelivery.feeservice.representation.ErrorResponse;

import com.fujitsu.fooddelivery.feeservice.representation.FeeResponse;
import com.fujitsu.fooddelivery.feeservice.service.FeeCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * FeeCalculationController is responsible for handling API consumer requests and giving out correct responses
 */
@RestController
public class FeeCalculationController {
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private WeatherObservationRepository weatherObservationRepository;
    private Logger logger;

    public FeeCalculationController() {
        logger = Logger.getLogger(FeeCalculationController.class.getName());
    }

    /**
     * GET request controller for /api/courierfee endpoint
     * @param city specifies a city URL variable, which must reference a valid location in the database
     * @param vehicle specifies a vehicle URL variable, which must be one of following values: "car", "scooter", "bike"
     * @return a response entity that either contains calculated fee value if the request was successful or an error message.
     */
    @GetMapping("/api/courierfee")
    public ResponseEntity<?> courierfeeEndpoint(@RequestParam(value = "city", defaultValue = "") String city,
                                                @RequestParam(value = "vehicle", defaultValue = "") String vehicle,
                                                @RequestParam(value = "unixTimestamp", defaultValue = "") String unixTimestamp)
    {
        logger.info("Request made to endpoint /api/courierfee");
        if (this.locationRepository.existsByCity(city)) {
            VehicleType type;
            try {
                type = VehicleType.valueOf(vehicle.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                logger.warning("Invalid vehicle argument given in '/api/courierfee/' endpoint");
                return new ResponseEntity<>(new ErrorResponse("Invalid vehicle argument '" + vehicle + "'", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
            }
            Location location = this.locationRepository.findByCity(city).get();
            FeeCalculationService feeCalculationService = new FeeCalculationService();

            Optional<WeatherObservation> observation;
            try {
                long timestamp = Long.parseLong(unixTimestamp);
                LocalDateTime ldt = Instant.ofEpochSecond(timestamp).atOffset(ZoneOffset.UTC).toLocalDateTime();
                logger.info("Querying the most recent WeatherObservation entry at timestamp " + ldt);
                observation = weatherObservationRepository.findFirstByStationAndTimestampLessThanEqualOrderByTimestampDesc(location.getWeatherStation(), ldt);
            }
            catch (NumberFormatException e) {
                logger.info("Querying the most recent WeatherObservation entry from given weather station");
                observation = weatherObservationRepository.findFirstByStationOrderByTimestampDesc(location.getWeatherStation());
            }

            try {
                BigDecimal fee = feeCalculationService.calculate(location, type, observation.orElse(null));
                return new ResponseEntity<>(new FeeResponse(fee, location.getCurrency()), HttpStatus.OK);
            }
            catch (ForbiddenVehicleException e) {
                return new ResponseEntity<>(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>(new ErrorResponse("Invalid city", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }
}
