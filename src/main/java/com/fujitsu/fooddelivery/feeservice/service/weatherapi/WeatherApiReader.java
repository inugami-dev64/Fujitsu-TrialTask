package com.fujitsu.fooddelivery.feeservice.service.weatherapi;

import com.fujitsu.fooddelivery.feeservice.exception.WeatherApiException;
import com.fujitsu.fooddelivery.feeservice.exception.WeatherStationNotFoundException;
import com.fujitsu.fooddelivery.feeservice.model.WeatherObservation;
import com.fujitsu.fooddelivery.feeservice.model.WeatherStation;

import java.util.List;

public interface WeatherApiReader {
    /**
     * Finds the most recent observation made by given weather station
     * @param station station whose observation to query
     * @return a WeatherObservation object containing data about the most recent observation
     * @throws WeatherApiException
     */
    WeatherObservation findTheMostRecentObservationByStation(WeatherStation station) throws WeatherApiException;

    /**
     * Find a a weather station by specified station's name
     * @param name station's name to use for searching
     * @return a valid WeatherStation object if such station exists or null of it doesn't exist
     */
    WeatherStation findWeatherStationByName(String name);

    /**
     * Query all weather stations supported by implementation specific API
     * @return a list containing all WeatherStation objects that were returned by the API
     * @throws WeatherApiException
     */
    List<WeatherStation> findAllStations() throws WeatherApiException;
}
