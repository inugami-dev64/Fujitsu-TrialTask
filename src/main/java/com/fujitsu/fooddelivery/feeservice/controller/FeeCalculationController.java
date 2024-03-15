package com.fujitsu.fooddelivery.feeservice.controller;

import com.fujitsu.fooddelivery.feeservice.exception.ForbiddenVehicleException;
import com.fujitsu.fooddelivery.feeservice.model.Location;
import com.fujitsu.fooddelivery.feeservice.model.VehicleType;
import com.fujitsu.fooddelivery.feeservice.model.WeatherObservation;
import com.fujitsu.fooddelivery.feeservice.model.repository.LocationRepository;
import com.fujitsu.fooddelivery.feeservice.model.repository.WeatherObservationRepository;
import com.fujitsu.fooddelivery.feeservice.representation.BadRequestErrorResponse;
import com.fujitsu.fooddelivery.feeservice.representation.ErrorResponse;

import com.fujitsu.fooddelivery.feeservice.representation.FeeResponse;
import com.fujitsu.fooddelivery.feeservice.service.FeeCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.NoSuchElementException;
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
    @Autowired
    private FeeCalculationService feeCalculationService;
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
        try {
            VehicleType type = VehicleType.valueOf(vehicle.toUpperCase()); // can throw IllegalArgumentException
            Location location = this.locationRepository.findByCity(city).get(); // can throw NoSuchElementException
            Optional<WeatherObservation> optObservation;
            try {
                // try to parse unixTimestamp url variable
                long timestamp = Long.parseLong(unixTimestamp);
                LocalDateTime ldt = Instant.ofEpochSecond(timestamp).atOffset(ZoneOffset.UTC).toLocalDateTime();
                logger.info("Querying the most recent WeatherObservation entry at timestamp " + ldt);
                optObservation = weatherObservationRepository.findFirstByStationAndTimestampLessThanEqualOrderByTimestampDesc(location.getWeatherStation(), ldt);
            }
            catch (NumberFormatException e) {
                // if it fails then just query the most recent WeatherObservation
                logger.info("Querying the most recent WeatherObservation entry");
                optObservation = weatherObservationRepository.findFirstByStationOrderByTimestampDesc(location.getWeatherStation());
            }
            BigDecimal fee = feeCalculationService.calculate(location, type, optObservation.orElse(null)); // can throw ForbiddenVehicleException
            return ResponseEntity.ok(new FeeResponse(fee, location.getCurrency()));
        }
        catch (NoSuchElementException e) {
            logger.warning("Could not find city with name '" + city + "'");
            return ResponseEntity.badRequest().body(new BadRequestErrorResponse("Invalid city name '" + city + "'"));
        }
        catch (IllegalArgumentException e) {
            logger.warning("Invalid vehicle argument '" + vehicle.toLowerCase() + "' given to '/api/courierfee/' endpoint");
            return ResponseEntity.badRequest().body(new BadRequestErrorResponse("Invalid vehicle argument '" + vehicle + "'"));
        }
        catch (ForbiddenVehicleException e) {
            logger.warning("Usage of selected vehicle " + vehicle.toLowerCase() + " is forbidden in '" + city);
            return ResponseEntity.badRequest().body(new BadRequestErrorResponse(e.getMessage()));
        }
    }
}
