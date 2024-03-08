package com.fujitsu.fooddelivery.feeservice.controller;

import com.fujitsu.fooddelivery.feeservice.model.Location;
import com.fujitsu.fooddelivery.feeservice.model.repository.LocationRepository;
import com.fujitsu.fooddelivery.feeservice.representation.ErrorResponse;

import com.fujitsu.fooddelivery.feeservice.representation.FeeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * FeeCalculationController is responsible for handling API consumer requests and giving out correct responses
 */
@RestController
@RequestMapping("/api")
public class FeeCalculationController {
    @Autowired
    private LocationRepository locationRepository;


    private boolean isCorrectVehicle(String vehicle) {
        return vehicle.equalsIgnoreCase("car") || vehicle.equalsIgnoreCase("scooter") || vehicle.equalsIgnoreCase("bike");
    }

    /**
     * GET request controller for /api/courierfee endpoint
     * @param city specifies a city URL variable, which must reference a valid location in the database
     * @param vehicle specifies a vehicle URL variable, which must be one of following values: "car", "scooter", "bike"
     * @return
     */
    @GetMapping("/courierfee")
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
            return new ResponseEntity<>(new FeeResponse(rbf, location.getCurrency()), HttpStatus.OK);
        }

        return new ResponseEntity<>(new ErrorResponse("Invalid city", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }
}
