package com.fujitsu.fooddelivery.feeservice.controller;

import com.fujitsu.fooddelivery.feeservice.exception.WeatherApiResponseException;
import com.fujitsu.fooddelivery.feeservice.model.Location;
import com.fujitsu.fooddelivery.feeservice.model.WeatherStation;
import com.fujitsu.fooddelivery.feeservice.model.repository.LocationRepository;
import com.fujitsu.fooddelivery.feeservice.model.repository.WeatherStationRepository;
import com.fujitsu.fooddelivery.feeservice.representation.ErrorResponse;
import com.fujitsu.fooddelivery.feeservice.representation.SuccessResponse;
import com.fujitsu.fooddelivery.feeservice.representation.WeatherStationRequestBody;
import com.fujitsu.fooddelivery.feeservice.weatherapi.IlmateenistusApiReader;
import com.fujitsu.fooddelivery.feeservice.weatherapi.WeatherApiReader;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/admin/weatherstation")
public class WeatherStationCrudController {
    @Autowired
    private WeatherStationRepository weatherStationRepository;
    @Autowired
    private LocationRepository locationRepository;

    private final Logger logger = Logger.getLogger(WeatherStationCrudController.class.getName());

    /**
     * Create a new
     * @param body
     * @return
     */
    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> create(@RequestBody WeatherStationRequestBody body) {
        // check if the weather api already exists
        if (this.weatherStationRepository.findByName(body.name()) != null) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("Weather station already exists in the database", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }

        WeatherApiReader api;
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new URL(IlmateenistusApiReader.ENDPOINT));
            api = new IlmateenistusApiReader(document);
        }
        catch (MalformedURLException e) {
            logger.severe(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse("Could not connect to the external weather API", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (DocumentException e) {
            logger.severe(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse("Could not parse XML response given by external weather API", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        WeatherStation station = api.findWeatherStationByName(body.name());
        Location location = this.locationRepository.findByCity(body.location());
        if (station == null) {
            return new ResponseEntity<>(new ErrorResponse("Invalid weather station name", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }
        if (location == null) {
            return new ResponseEntity<>(new ErrorResponse("Invalid location name", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }

        location.setWeatherStation(station);
        this.weatherStationRepository.save(station);
        this.locationRepository.flush();

        return new ResponseEntity<SuccessResponse>(new SuccessResponse(), HttpStatus.OK);
    }
}
