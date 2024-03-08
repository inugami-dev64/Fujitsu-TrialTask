package com.fujitsu.fooddelivery.feeservice.controller;

import com.fujitsu.fooddelivery.feeservice.exception.WeatherApiResponseException;
import com.fujitsu.fooddelivery.feeservice.model.*;
import com.fujitsu.fooddelivery.feeservice.model.repository.*;
import com.fujitsu.fooddelivery.feeservice.representation.ErrorResponse;
import com.fujitsu.fooddelivery.feeservice.representation.SuccessResponse;
import com.fujitsu.fooddelivery.feeservice.weatherapi.WeatherAPI;
import com.fujitsu.fooddelivery.feeservice.weatherapi.IlmateenistusApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/admin/data")
public class DatabaseSeedController {
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private WeatherStationRepository weatherStationRepository;
    @Autowired
    private RegionalBaseFeeRepository regionalBaseFeeRepository;
    @Autowired
    private ExtraFeeRepository extraFeeRepository;

    private Logger logger = Logger.getLogger(DatabaseSeedController.class.getName());

    private Set<ExtraFee> generateExtraFees() {
        Set<ExtraFee> extraFees = new HashSet<>();
        extraFees.add(new AirTemperatureExtraFee(new BigDecimal("1.00"), VehicleRule.NOT_APPLICABLE, VehicleRule.APPLICABLE, VehicleRule.APPLICABLE, null, -10.0f));
        extraFees.add(new AirTemperatureExtraFee(new BigDecimal("0.50"), VehicleRule.NOT_APPLICABLE, VehicleRule.APPLICABLE, VehicleRule.APPLICABLE, -10.f, 0.f));
        extraFees.add(new WindSpeedExtraFee(new BigDecimal("0.50"), VehicleRule.NOT_APPLICABLE, VehicleRule.NOT_APPLICABLE, VehicleRule.APPLICABLE, 10.f, 20.f));
        extraFees.add(new WindSpeedExtraFee(new BigDecimal("0.50"), VehicleRule.NOT_APPLICABLE, VehicleRule.NOT_APPLICABLE, VehicleRule.FORBIDDEN, 20.f, null));
        extraFees.add(new WeatherPhenomenonExtraFee(new BigDecimal("1.00"), VehicleRule.NOT_APPLICABLE, VehicleRule.APPLICABLE, VehicleRule.APPLICABLE, WeatherPhenomenonClassification.SNOW));
        extraFees.add(new WeatherPhenomenonExtraFee(new BigDecimal("1.00"), VehicleRule.NOT_APPLICABLE, VehicleRule.APPLICABLE, VehicleRule.APPLICABLE, WeatherPhenomenonClassification.SLEET));
        extraFees.add(new WeatherPhenomenonExtraFee(new BigDecimal("0.50"), VehicleRule.NOT_APPLICABLE, VehicleRule.APPLICABLE, VehicleRule.APPLICABLE, WeatherPhenomenonClassification.RAIN));
        extraFees.add(new WeatherPhenomenonExtraFee(new BigDecimal("0.00"), VehicleRule.NOT_APPLICABLE, VehicleRule.FORBIDDEN, VehicleRule.FORBIDDEN, WeatherPhenomenonClassification.GLAZE));
        extraFees.add(new WeatherPhenomenonExtraFee(new BigDecimal("0.00"), VehicleRule.NOT_APPLICABLE, VehicleRule.FORBIDDEN, VehicleRule.FORBIDDEN, WeatherPhenomenonClassification.HAIL));
        extraFees.add(new WeatherPhenomenonExtraFee(new BigDecimal("0.00"), VehicleRule.NOT_APPLICABLE, VehicleRule.FORBIDDEN, VehicleRule.FORBIDDEN, WeatherPhenomenonClassification.THUNDER));
        return extraFees;
    }

    private List<Location> generateLocations(Set<ExtraFee> extraFees) throws MalformedURLException, WeatherApiResponseException {
        WeatherAPI api = new IlmateenistusApi();
        List<Location> locations = new ArrayList<>();
        // Tallinn
        locations.add(new Location());
        locations.get(locations.size()-1).setCity("Tallinn");
        locations.get(locations.size()-1).setCountry("Estonia");
        locations.get(locations.size()-1).setCurrency("EUR");
        locations.get(locations.size()-1).setWeatherStation(api.findWeatherStationByName("Tallinn-Harku"));
        locations.get(locations.size()-1).setRegionalBaseFee(new RegionalBaseFee());
        locations.get(locations.size()-1).getRegionalBaseFee().setCar(new BigDecimal("4.00"));
        locations.get(locations.size()-1).getRegionalBaseFee().setScooter(new BigDecimal("3.50"));
        locations.get(locations.size()-1).getRegionalBaseFee().setCar(new BigDecimal("3.00"));
        locations.get(locations.size()-1).setExtraFees(extraFees);

        // Tartu
        locations.add(new Location());
        locations.get(locations.size()-1).setCity("Tartu");
        locations.get(locations.size()-1).setCountry("Estonia");
        locations.get(locations.size()-1).setCurrency("EUR");
        locations.get(locations.size()-1).setWeatherStation(api.findWeatherStationByName("Tartu-Tõravere"));
        locations.get(locations.size()-1).setRegionalBaseFee(new RegionalBaseFee());
        locations.get(locations.size()-1).getRegionalBaseFee().setCar(new BigDecimal("3.50"));
        locations.get(locations.size()-1).getRegionalBaseFee().setScooter(new BigDecimal("3.00"));
        locations.get(locations.size()-1).getRegionalBaseFee().setBike(new BigDecimal("2.50"));
        locations.get(locations.size()-1).setExtraFees(extraFees);

        // Pärnu
        locations.add(new Location());
        locations.get(locations.size()-1).setCity("Pärnu");
        locations.get(locations.size()-1).setCountry("Estonia");
        locations.get(locations.size()-1).setCurrency("EUR");
        locations.get(locations.size()-1).setWeatherStation(api.findWeatherStationByName("Pärnu"));
        locations.get(locations.size()-1).setRegionalBaseFee(new RegionalBaseFee());
        locations.get(locations.size()-1).getRegionalBaseFee().setCar(new BigDecimal("3.00"));
        locations.get(locations.size()-1).getRegionalBaseFee().setCar(new BigDecimal("2.50"));
        locations.get(locations.size()-1).getRegionalBaseFee().setCar(new BigDecimal("2.00"));
        locations.get(locations.size()-1).setExtraFees(extraFees);

        return locations;
    }

    @GetMapping("/init")
    public ResponseEntity<?> seedDatabase() {
        if (locationRepository.count() != 0 || weatherStationRepository.count() != 0 || extraFeeRepository.count() != 0)
        {
            this.logger.warning("An attempt was made to initialize non-empty database");
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("Cannot initialize non-empty database", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }

        Set<ExtraFee> extraFees = this.generateExtraFees();
        extraFeeRepository.saveAll(extraFees);

        List<Location> locations;
        try {
            locations = this.generateLocations(extraFees);
        }
        catch (MalformedURLException e) {
            this.logger.severe(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse("Failed to initialize the database", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (WeatherApiResponseException e) {
            this.logger.severe("Weather API's response was unexpected: " + e.getMessage());
            return new ResponseEntity<>(new ErrorResponse("Failed to initialize the database", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // for each location save RBF, and weather station
        for (Location location : locations) {
            this.regionalBaseFeeRepository.save(location.getRegionalBaseFee());
            weatherStationRepository.save(location.getWeatherStation());
        }

        this.locationRepository.saveAll(locations);
        return new ResponseEntity<>(new SuccessResponse(), HttpStatus.OK);
    }
}
