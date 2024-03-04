package com.fujitsu.fooddelivery.feeservice.controller;

import com.fujitsu.fooddelivery.feeservice.representation.response.ErrorResponse;
import com.fujitsu.fooddelivery.feeservice.representation.response.FeeResponse;
import com.fujitsu.fooddelivery.feeservice.representation.response.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * FeeCalculationController is responsible for handling API consumer requests and giving out correct responses
 */
@RestController
public class FeeCalculationController {
    @GetMapping("/api/courierfee")
    public Response courierfeeEndpoint(@RequestParam(value = "city", defaultValue = "") String city,
                                       @RequestParam(value = "vehicle", defaultValue = "") String vehicle) {

        BigDecimal regionalBaseFee;
        if (city.equalsIgnoreCase("Tartu")) {
            if (vehicle.equalsIgnoreCase("Car")) {
                return new FeeResponse(new BigDecimal("3.5"), "EUR");
            }
            else if (vehicle.equalsIgnoreCase("Scooter")) {
                return new FeeResponse(new BigDecimal("3"), "EUR");
            }
            else if (vehicle.equalsIgnoreCase("Bike")) {
                return new FeeResponse(new BigDecimal("2.5"), "EUR");
            }
        }
        else if (city.equalsIgnoreCase("Tallinn")) {
            if (vehicle.equalsIgnoreCase("Car")) {
                return new FeeResponse(new BigDecimal("4"), "EUR");
            }
            else if (vehicle.equalsIgnoreCase("Scooter")) {
                return new FeeResponse(new BigDecimal("3.5"), "EUR");
            }
            else if (vehicle.equalsIgnoreCase("Bike")) {
                return new FeeResponse(new BigDecimal("3"), "EUR");
            }
        }
        else if (city.equalsIgnoreCase("Pärnu")) {
            if (vehicle.equalsIgnoreCase("Car")) {
                return new FeeResponse(new BigDecimal("3"), "EUR");
            }
            else if (vehicle.equalsIgnoreCase("Scooter")) {
                return new FeeResponse(new BigDecimal("2.5"), "EUR");
            }
            else if (vehicle.equalsIgnoreCase("Bike")) {
                return new FeeResponse(new BigDecimal("2"), "EUR");
            }
        } else {
            return new ErrorResponse(1, "INVALID_CITY_CODE");
        }

        return new ErrorResponse(2, "INVALID_VEHICLE_TYPE");
    }
}
