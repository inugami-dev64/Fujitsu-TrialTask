package com.fujitsu.fooddelivery.feeservice.model;

/**
 * Enum values to define how extra fee rules affect vehicles
 * NOT_QUALIFIED - the vehicle type is not qualified to receive the extra fee
 * QUALIFIED - the vehicle type is qualified to receive the extra fee
 * FORBIDDEN - the vehicle cannot be used to make deliveries under given extra fee condition
 */
public enum VehicleRule {
    NOT_QUALIFIED,
    QUALIFIED,
    FORBIDDEN
}
