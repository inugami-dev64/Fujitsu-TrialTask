package com.fujitsu.fooddelivery.feeservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.Set;
@Entity
@Table(name = "atef")
@NoArgsConstructor
public class AirTemperatureExtraFee extends ExtraFee {
    @Getter
    @Setter
    private Float minTemperature;

    @Getter
    @Setter
    private Float maxTemperature;

    public AirTemperatureExtraFee(BigDecimal extraFee, VehicleRule carRule, VehicleRule scooterRule, VehicleRule bikeRule, Float minTemperature, Float maxTemperature) {
        super(extraFee, carRule, scooterRule, bikeRule);
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
    }

    public AirTemperatureExtraFee(LocalDateTime validFrom, LocalDateTime expireTime, BigDecimal extraFee, VehicleRule carRule, VehicleRule scooterRule, VehicleRule bikeRule, Float minTemperature, Float maxTemperature) {
        super(validFrom, expireTime, extraFee, carRule, scooterRule, bikeRule);
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
    }

    @Override
    public boolean matchesObservation(WeatherObservation observation) {
        return observation.getAirtemperature() != null &&
                (minTemperature == null || observation.getAirtemperature() >= minTemperature) &&
                (maxTemperature == null || observation.getAirtemperature() < maxTemperature);
    }
}
