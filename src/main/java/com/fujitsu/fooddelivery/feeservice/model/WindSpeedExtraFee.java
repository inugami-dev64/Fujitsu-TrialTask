package com.fujitsu.fooddelivery.feeservice.model;

import jakarta.persistence.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.Set;

@Entity
@Table(name = "wsef")
@NoArgsConstructor
public class WindSpeedExtraFee extends ExtraFee {
    @Getter
    @Setter
    private Float minWindSpeed;

    @Getter
    @Setter
    private Float maxWindSpeed;

    public WindSpeedExtraFee(BigDecimal extraFee, VehicleRule carRule, VehicleRule scooterRule, VehicleRule bikeRule, Float minWindSpeed, Float maxWindSpeed) {
        super(extraFee, carRule, scooterRule, bikeRule);
        this.minWindSpeed = minWindSpeed;
        this.maxWindSpeed = maxWindSpeed;
    }

    public WindSpeedExtraFee(LocalDateTime validFrom, LocalDateTime expireTime, BigDecimal extraFee, VehicleRule carRule, VehicleRule scooterRule, VehicleRule bikeRule, Float minWindSpeed, Float maxWindSpeed) {
        super(validFrom, expireTime, extraFee, carRule, scooterRule, bikeRule);
        this.minWindSpeed = minWindSpeed;
        this.maxWindSpeed = maxWindSpeed;
    }

    @Override
    public boolean matchesObservation(WeatherObservation observation) {
        return observation.getWindSpeed() != null &&
                (minWindSpeed == null || observation.getWindSpeed() >= minWindSpeed) &&
                (maxWindSpeed == null || observation.getWindSpeed() < maxWindSpeed);
    }
}
