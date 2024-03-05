package com.fujitsu.fooddelivery.feeservice.controller;

import com.fujitsu.fooddelivery.feeservice.model.Location;
import com.fujitsu.fooddelivery.feeservice.model.repository.LocationRepository;
import com.fujitsu.fooddelivery.feeservice.model.repository.WeatherApiRepository;
import com.fujitsu.fooddelivery.feeservice.representation.ErrorResponse;

import com.fujitsu.fooddelivery.feeservice.representation.FeeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * FeeCalculationController is responsible for handling API consumer requests and giving out correct responses
 */
@RestController
public class FeeCalculationController {
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private WeatherApiRepository weatherApiRepository;

    private boolean isCorrectVehicle(String vehicle) {
        return vehicle.equalsIgnoreCase("car") || vehicle.equalsIgnoreCase("scooter") || vehicle.equalsIgnoreCase("bike");
    }

    @GetMapping("/api/courierfee")
    public ResponseEntity<?> courierfeeEndpoint(@RequestParam(value = "city", defaultValue = "") String city,
                                                @RequestParam(value = "vehicle", defaultValue = "") String vehicle)
    {
        if (!this.isCorrectVehicle(vehicle)) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("Invalid vehicle type", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }

        if (this.locationRepository.existsByCity(city)) {
            Location location = this.locationRepository.findByCity(city);
            BigDecimal rbf;
            if (vehicle.equalsIgnoreCase("car")) {
                rbf = location.getRegionalBaseFee().getCar();
            }
            else if (vehicle.equalsIgnoreCase("scooter")) {
                rbf = location.getRegionalBaseFee().getScooter();
            }
            else {
                rbf = location.getRegionalBaseFee().getBike();
            }
            return new ResponseEntity<FeeResponse>(new FeeResponse(rbf, location.getRegionalBaseFee().getCurrency()), HttpStatus.OK);
        }

        return new ResponseEntity<ErrorResponse>(new ErrorResponse("Invalid city", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }
}
