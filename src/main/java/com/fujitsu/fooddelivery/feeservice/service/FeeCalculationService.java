package com.fujitsu.fooddelivery.feeservice.service;

import com.fujitsu.fooddelivery.feeservice.exception.ForbiddenVehicleException;
import com.fujitsu.fooddelivery.feeservice.model.Location;
import com.fujitsu.fooddelivery.feeservice.model.VehicleType;
import com.fujitsu.fooddelivery.feeservice.model.WeatherObservation;

import java.math.BigDecimal;

public interface FeeCalculationService {
    /**
     * Calculates the courier fee based on location, vehicle type and given weather observation
     * @param location specifies a valid Location object that is used for the calculation
     * @param type specifies the type of vehicle to use
     * @param observation specifies the weather observation that is used to calculate extra fees for the courier
     * @return BigDecimal object describing the calculated fee
     * @throws ForbiddenVehicleException
     */
    BigDecimal calculate(Location location, VehicleType type, WeatherObservation observation) throws ForbiddenVehicleException;
}
