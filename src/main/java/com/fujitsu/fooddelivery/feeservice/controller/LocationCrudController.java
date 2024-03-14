package com.fujitsu.fooddelivery.feeservice.controller;

import com.fujitsu.fooddelivery.feeservice.exception.InvalidIdentifierException;
import com.fujitsu.fooddelivery.feeservice.model.Location;
import com.fujitsu.fooddelivery.feeservice.representation.BadRequestErrorResponse;
import com.fujitsu.fooddelivery.feeservice.representation.ErrorResponse;
import com.fujitsu.fooddelivery.feeservice.representation.SuccessResponse;
import com.fujitsu.fooddelivery.feeservice.service.LocationCrudService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
     * @param location specifies the user provided request body that should represent a valid Location object
     * @return saved location object
     */
    @PostMapping("")
    public ResponseEntity<Location> createLocation(@Valid @RequestBody Location location) {
        return ResponseEntity.ok(locationCrudService.saveLocation(location));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLocation(@RequestBody Location location, @PathVariable("id") Integer id) throws InvalidIdentifierException {
        return ResponseEntity.ok(locationCrudService.updateLocation(location, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteLocation(@PathVariable("id") Integer id) throws InvalidIdentifierException {
        locationCrudService.deleteById(id);
        return ResponseEntity.ok(new SuccessResponse());
    }

    @ExceptionHandler(value = InvalidIdentifierException.class)
    public ResponseEntity<ErrorResponse> handleInvalidIdentifierException(InvalidIdentifierException e) {
        return ResponseEntity.badRequest().body(new BadRequestErrorResponse(e.getMessage()));
    }
}
