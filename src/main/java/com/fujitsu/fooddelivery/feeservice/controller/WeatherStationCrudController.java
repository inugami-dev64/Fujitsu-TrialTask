package com.fujitsu.fooddelivery.feeservice.controller;

import com.fujitsu.fooddelivery.feeservice.model.repository.WeatherStationRepository;
import com.fujitsu.fooddelivery.feeservice.representation.ErrorResponse;
import com.fujitsu.fooddelivery.feeservice.representation.WeatherStationRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weatherstation")
public class WeatherStationCrudController {
    @Autowired
    private WeatherStationRepository weatherStationRepository;

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> create(@RequestBody WeatherStationRequestBody body) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse("Invalid method", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
