package com.fujitsu.fooddelivery.feeservice.service;

import com.fujitsu.fooddelivery.feeservice.model.WeatherStation;

/**
 * WeatherStationQueryService is responsible for querying weather stations from both external APIs and
 * the persistence repository
 */
public interface WeatherStationQueryService {
    /**
     * Attempt to find a weather station by given ID from the persistence repository without using any external APIs
     * @param id specifies the ID to use for querying
     * @return a WeatherStation object or null if the entry with given ID did not exist
     */
    WeatherStation findById(Integer id);

    /**
     * Attempt to find a weather station by given name from both the persistence repository and external APIs
     * @param name specifies the given name to use for querying
     * @return a WeatherStation object or null if the name didn't exist in any data sources
     */
    WeatherStation findByName(String name);

    /**
     * Attempt to find a weather station by given wmo code from both the persistence repository and external APIs
     * @param wmo specifies the given name to use for querying
     * @return a WeatherStation object or null if the name didn't exist in any data sources
     */
    WeatherStation findByWmoCode(Integer wmo);
}
