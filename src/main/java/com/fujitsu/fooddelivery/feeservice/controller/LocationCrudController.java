package com.fujitsu.fooddelivery.feeservice.controller;

import com.fujitsu.fooddelivery.feeservice.exception.InvalidIdentifierException;
import com.fujitsu.fooddelivery.feeservice.exception.WeatherStationNotFoundException;
import com.fujitsu.fooddelivery.feeservice.model.Location;
import com.fujitsu.fooddelivery.feeservice.representation.BadRequestErrorResponse;
import com.fujitsu.fooddelivery.feeservice.representation.ErrorResponse;
import com.fujitsu.fooddelivery.feeservice.representation.SuccessResponse;
import com.fujitsu.fooddelivery.feeservice.representation.ValidationErrorResponse;
import com.fujitsu.fooddelivery.feeservice.service.LocationCrudService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/locations")
public class LocationCrudController {
    @Autowired
    LocationCrudService locationCrudService;
    Logger logger = Logger.getLogger(LocationCrudController.class.getName());

    /**
     * CRUD endpoint for querying data about a single location
     * @param id specifies the ID to use for querying
     * @return a ResponseEntity object containing the location object or an error response that describes invalid location ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getSingleLocation(@PathVariable("id") Integer id) {
        Location location = locationCrudService.getLocationById(id);
        if (location != null)
            return ResponseEntity.ok(location);

        return ResponseEntity.badRequest().body(new BadRequestErrorResponse("Invalid location ID"));
    }

    /**
     * CRUD endpoint for querying all possible locations in the database
     * @return a ResponseEntity object containing a list of all locations
     */
    @GetMapping("")
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(locationCrudService.getAllLocations());
    }

    /**
     * CRUD endpoint for creating new locations.
     * @param location specifies the user provided request body that must represent a valid Location object
     * @return location entity that was saved
     */
    @PostMapping("")
    public ResponseEntity<Location> createLocation(@Valid @RequestBody Location location) throws WeatherStationNotFoundException {
        return ResponseEntity.ok(locationCrudService.saveLocation(location));
    }

    /**
     * CRUD endpoint for updating existing location entries
     * @param location represents the location entity given as the request body JSON
     * @param id specifies the ID of the location entity that is going to be updated
     * @return updated location entity if the update was successful
     * @throws InvalidIdentifierException gets thrown when provided identifier is invalid
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLocation(@RequestBody Location location, @PathVariable("id") Integer id) throws InvalidIdentifierException {
        return ResponseEntity.ok(locationCrudService.updateLocation(location, id));
    }

    /**
     * CRUD endpoint for deleting location entries
     * @param id specifies the ID of the location entity that gets deleted
     * @return success message if deletion was successful and no exceptions were thrown
     * @throws InvalidIdentifierException gets thrown when provided identifier is invalid
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteLocation(@PathVariable("id") Integer id) throws InvalidIdentifierException {
        locationCrudService.deleteById(id);
        return ResponseEntity.ok(new SuccessResponse());
    }

    /**
     * Exception handler for InvalidIdentifierException type exceptions
     * @param e specifies the thrown exception object
     * @return a descriptive error message with BAD_REQUEST status code in JSON format
     */
    @ExceptionHandler(value = InvalidIdentifierException.class)
    public ResponseEntity<ErrorResponse> handleInvalidIdentifierException(InvalidIdentifierException e) {
        logger.warning("InvalidIdentifierException thrown at LocationCrudController: " + e.getMessage());
        return ResponseEntity.badRequest().body(new BadRequestErrorResponse(e.getMessage()));
    }

    /**
     * Exception handler for MethodArgumentNotValidException type exceptions
     * @param e specifies the thrown exception object
     * @return a descriptive error message with BAD_REQUEST status code in JSON format
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.warning("MethodArgumentNotValidException thrown at LocationCrudController: " + e.getMessage());

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldError = ((FieldError)error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldError, errorMessage);
        });

        return ResponseEntity.badRequest().body(new ValidationErrorResponse(errors));
    }
}
