package com.fujitsu.fooddelivery.feeservice.controller;

import com.fujitsu.fooddelivery.feeservice.model.*;
import com.fujitsu.fooddelivery.feeservice.model.repository.*;
import com.fujitsu.fooddelivery.feeservice.representation.BadRequestErrorResponse;
import com.fujitsu.fooddelivery.feeservice.representation.ErrorResponse;
import com.fujitsu.fooddelivery.feeservice.representation.SuccessResponse;
import com.fujitsu.fooddelivery.feeservice.service.weatherapi.WeatherApiReader;
import com.fujitsu.fooddelivery.feeservice.service.weatherapi.IlmateenistusApiReader;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * DatabaseSeedController is responsible for keeping an endpoint to use for database initialization
 */
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

    private final Logger logger = Logger.getLogger(DatabaseSeedController.class.getName());

    private List<ExtraFee> generateExtraFees() {
        List<ExtraFee> extraFees = new ArrayList<>();
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

    private List<Location> generateLocations() throws MalformedURLException, DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new URL(IlmateenistusApiReader.ENDPOINT));
        WeatherApiReader api = new IlmateenistusApiReader(document);
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
        locations.get(locations.size()-1).getRegionalBaseFee().setBike(new BigDecimal("3.00"));
        locations.get(locations.size()-1).setExtraFees(generateExtraFees());

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
        locations.get(locations.size()-1).setExtraFees(generateExtraFees());

        // Pärnu
        locations.add(new Location());
        locations.get(locations.size()-1).setCity("Pärnu");
        locations.get(locations.size()-1).setCountry("Estonia");
        locations.get(locations.size()-1).setCurrency("EUR");
        locations.get(locations.size()-1).setWeatherStation(api.findWeatherStationByName("Pärnu"));
        locations.get(locations.size()-1).setRegionalBaseFee(new RegionalBaseFee());
        locations.get(locations.size()-1).getRegionalBaseFee().setCar(new BigDecimal("3.00"));
        locations.get(locations.size()-1).getRegionalBaseFee().setScooter(new BigDecimal("2.50"));
        locations.get(locations.size()-1).getRegionalBaseFee().setBike(new BigDecimal("2.00"));
        locations.get(locations.size()-1).setExtraFees(generateExtraFees());

        return locations;
    }

    /**
     * GET request endpoint to initially seed the database with data that was specified in the requirements document
     * @return a response entity that either contains SuccessResponse when the seeding was successfully or ErrorResponse otherwise
     */
    @GetMapping("/init")
    public ResponseEntity<?> seedDatabase() {
        this.logger.info("Attempting to seed the database");
        if (locationRepository.count() != 0 || weatherStationRepository.count() != 0 || extraFeeRepository.count() != 0)
        {
            this.logger.warning("An attempt was made to initialize non-empty database");
            return ResponseEntity.notFound().build();
        }

        // try to generate and save all locations
        try {
            List<Location> locations = this.generateLocations();
            this.locationRepository.saveAll(locations);
            return new ResponseEntity<>(new SuccessResponse(), HttpStatus.OK);
        }
        catch (MalformedURLException e) {
            this.logger.severe(e.getMessage());
            return ResponseEntity.badRequest().body(new BadRequestErrorResponse("Failed to initialize the database"));
        }
        catch (DocumentException e) {
            this.logger.severe("Could not create instance of Document: " + e.getMessage());
            return ResponseEntity.badRequest().body(new BadRequestErrorResponse("Failed to initialize the database"));
        }
    }
}
