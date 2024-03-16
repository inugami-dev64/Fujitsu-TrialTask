package com.fujitsu.fooddelivery.feeservice.service;

import com.fujitsu.fooddelivery.feeservice.exception.InvalidIdentifierException;
import com.fujitsu.fooddelivery.feeservice.exception.WeatherStationNotFoundException;
import com.fujitsu.fooddelivery.feeservice.model.Location;

import java.util.List;

public interface LocationCrudService {
    /**
     * Persists the given Location object
     * @param location object to persist
     * @return object that was persisted
     * @throws WeatherStationNotFoundException
     */
    Location saveLocation(Location location) throws WeatherStationNotFoundException;
    /**
     * Fetches and returns all locations from the persistent storage
     * @return list of all Location objects that were fetched
     */
    List<Location> getAllLocations();
    /**
     * Attempts to find and return a location with given ID
     * @param id specifies the ID to use for query
     * @return a valid location object if such entry was found, null otherwise
     */
    Location getLocationById(Integer id);
    /**
     * Updates the given location with new data
     * @param location specifies the new data to use for updating a persistent object
     * @param id specifies the location ID of which to update
     * @return updated Location object
     */
    Location updateLocation(Location location, Integer id) throws InvalidIdentifierException;

    /**
     * Deletes the location entry from persistent storage
     * @param id specifies the location ID to delete
     */
    void deleteById(Integer id) throws InvalidIdentifierException;
}
