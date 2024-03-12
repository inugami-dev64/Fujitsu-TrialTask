package com.fujitsu.fooddelivery.feeservice.controller;

import com.fujitsu.fooddelivery.feeservice.exception.InvalidLocationException;
import com.fujitsu.fooddelivery.feeservice.model.ExtraFee;
import com.fujitsu.fooddelivery.feeservice.model.Location;
import com.fujitsu.fooddelivery.feeservice.model.RegionalBaseFee;
import com.fujitsu.fooddelivery.feeservice.model.WeatherStation;
import com.fujitsu.fooddelivery.feeservice.model.repository.ExtraFeeRepository;
import com.fujitsu.fooddelivery.feeservice.model.repository.LocationRepository;
import com.fujitsu.fooddelivery.feeservice.model.repository.RegionalBaseFeeRepository;
import com.fujitsu.fooddelivery.feeservice.model.repository.WeatherStationRepository;
import com.fujitsu.fooddelivery.feeservice.representation.ErrorResponse;
import com.fujitsu.fooddelivery.feeservice.representation.SimplifiedLocationResponse;
import com.fujitsu.fooddelivery.feeservice.representation.SuccessResponse;
import com.fujitsu.fooddelivery.feeservice.weatherapi.IlmateenistusApiReader;
import com.fujitsu.fooddelivery.feeservice.weatherapi.WeatherApiReader;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/location")
public class LocationCrudController {
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private RegionalBaseFeeRepository regionalBaseFeeRepository;
    @Autowired
    private WeatherStationRepository weatherStationRepository;
    @Autowired
    private ExtraFeeRepository extraFeeRepository;
    private Logger logger = Logger.getLogger(LocationCrudController.class.getName());

    private List<Location> queryLocations(String city) throws InvalidLocationException {
        if (city.isEmpty()) {
            return this.locationRepository.findAll();
        }
        List<Location> locations = new ArrayList<>();
        locations.add(this.locationRepository.findByCity(city));
        if (locations.get(locations.size() - 1) == null)
            throw new InvalidLocationException("Did not find location with city name '" + city + "'");
        return locations;
    }

    private List<SimplifiedLocationResponse> simplifyLocations(List<Location> locations) {
        List<SimplifiedLocationResponse> simplifiedLocationResponses = new ArrayList<>();
        for (Location location : locations) {
            simplifiedLocationResponses.add(new SimplifiedLocationResponse(location.getId(), location.getCountry(), location.getCity(), location.getCurrency()));
        }

        return simplifiedLocationResponses;
    }

    /**
     * Checks whether the user provided location variable contains sane information
     * It does not check, however, if the weather station location is correct, because this might require querying data from external weather API
     * @param location specifies the user supplied Location object
     * @return ErrorResponse if any errors were detected, null otherwise
     */
    private ErrorResponse creationSanityCheck(Location location) {
        // check if RBF is incompletely defined
        RegionalBaseFee rbf = location.getRegionalBaseFee();
        if (rbf.getCar() == null || rbf.getScooter() == null || rbf.getBike() == null) {
            return new ErrorResponse("Regional base fees must be specified for all vehicle types", HttpStatus.BAD_REQUEST.value());
        }

        if (rbf.getId() != null) {
            return new ErrorResponse("Regional base fee cannot be declared with an ID", HttpStatus.BAD_REQUEST.value());
        }

        // check if ID-based extra fees contain invalid IDs
        for (ExtraFee extraFee : location.getExtraFees()) {
            if (extraFee.getId() != null && !extraFeeRepository.existsById(extraFee.getId())) {
                return new ErrorResponse("Invalid extra fee ID", HttpStatus.BAD_REQUEST.value());
            }
        }

        // check if weather station is properly specified
        if (location.getWeatherStation().getId() != null && !weatherStationRepository.existsById(location.getWeatherStation().getId())) {
            return new ErrorResponse("Invalid weather station ID", HttpStatus.BAD_REQUEST.value());
        }
        else if (location.getWeatherStation().getName() == null) {
            return new ErrorResponse("Weather station must have its name or ID specified", HttpStatus.BAD_REQUEST.value());
        }

        return null;
    }

    private void saveNewLocationExtraFees(Location location) {
        Set<ExtraFee> replacementExtraFeeObjectSet = new HashSet<>();
        Set<ExtraFee> inputExtraFeeObjectSet = location.getExtraFees();

        for (ExtraFee extraFee : inputExtraFeeObjectSet) {
            if (extraFee.getId() != null) {
                replacementExtraFeeObjectSet.add(extraFeeRepository.findById(extraFee.getId()).get());
            }
            else {
                extraFeeRepository.save(extraFee);
                replacementExtraFeeObjectSet.add(extraFee);
            }
        }
    }

    private ErrorResponse saveWeatherStation(Location location) {
        if (location.getWeatherStation().getName() != null && !weatherStationRepository.existsByName(location.getWeatherStation().getName())) {
            try {
                SAXReader reader = new SAXReader();
                Document document = reader.read(new URL(IlmateenistusApiReader.ENDPOINT));
                WeatherApiReader weatherApiReader = new IlmateenistusApiReader(document);
                WeatherStation station;
                if ((station = weatherApiReader.findWeatherStationByName(location.getWeatherStation().getName())) == null) {
                    return new ErrorResponse("Invalid weather station name", HttpStatus.BAD_REQUEST.value());
                }
                else {
                    weatherStationRepository.save(station);
                    location.setWeatherStation(station);
                }
            }
            catch (DocumentException e) {
                logger.severe("Could not parse XML document returned by Ilmateenistus API: " + e.getMessage());
                return new ErrorResponse("Could not get data from external weather API", HttpStatus.INTERNAL_SERVER_ERROR.value();
            }
            catch (MalformedURLException e) {
                logger.severe("Could not create new URL object to endpoint '" + IlmateenistusApiReader.ENDPOINT + "': " + e.getMessage());
                return new ErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
        else if (location.getWeatherStation().getName() != null) {
            location.setWeatherStation(weatherStationRepository.findByName(location.getWeatherStation().getName()));
        }
        else if (location.getWeatherStation().getId() != null) {
            location.setWeatherStation(weatherStationRepository.findById(location.getWeatherStation().getId()).get());
        }

        return null;
    }

    /**
     * This REST controller endpoint allows users to query for available locations in two ways.<br>
     * The first way of querying is by simply querying for all possible locations that are available. In that case no 'city' variable should be specified in the URL.<br>
     * The second way of querying is by specifying the city name in which case the controller attempts to find given location and return information about it.
     * @param verbosity specifies a request parameter which determines how verbose should the response data should be. If given a value 'verbose' the controller
     *                  returns information about associated weather station, base fees, and extra fees in addition to the information related to the location itself.
     *                  In other cases the controller simply returns information about location(s) themselves.
     * @param city specifies the specific city name to query for in locations. This argument is completely optional
     * @return a response entity that either contains data about the locations or an error message if the request was invalid.
     */
    @GetMapping("/read")
    public ResponseEntity<?> getLocations(@RequestParam(value = "verbosity", defaultValue = "basic") String verbosity,
                                          @RequestParam(value = "city", defaultValue = "") String city)
    {
        // check if verbosity level is set to verbose
        if (verbosity.equals("verbose")) {
            try {
                List<Location> locations = queryLocations(city);
                return new ResponseEntity<>(locations, HttpStatus.OK);
            }
            catch (InvalidLocationException e) {
                this.logger.warning(e.getMessage());
                return new ResponseEntity<>(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
            }
        }

        try {
            List<Location> locations = queryLocations(city);
            return new ResponseEntity<>(simplifyLocations(locations), HttpStatus.OK);
        }
        catch (InvalidLocationException e) {
            this.logger.warning(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createLocation(@RequestBody Location location) {
        // perform sanity checks
        ErrorResponse errResponse = creationSanityCheck(location);
        if (errResponse != null) {
            logger.warning("Sanity check failed for user supplied Location object");
            return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
        }

        // attempt to save data about weather station
        errResponse = saveWeatherStation(location);
        if (errResponse != null)
            return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);

        regionalBaseFeeRepository.save(location.getRegionalBaseFee());
        saveNewLocationExtraFees(location);
        locationRepository.save(location);

        return new ResponseEntity<>(new SuccessResponse(), HttpStatus.OK);
    }
}
