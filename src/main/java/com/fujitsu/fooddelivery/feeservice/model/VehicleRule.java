package com.fujitsu.fooddelivery.feeservice.model;

/**
 * Enum values to define how extra fee rules affect vehicles
 * NOT_APPLICABLE - the vehicle type is not applicable to receive the extra fee
 * APPLICABLE - the vehicle type is applicable to receive the extra fee
 * FORBIDDEN - the vehicle cannot be used to make deliveries under given extra conditions
 */
public enum VehicleRule {
    NOT_APPLICABLE,
    APPLICABLE,
    FORBIDDEN
}
