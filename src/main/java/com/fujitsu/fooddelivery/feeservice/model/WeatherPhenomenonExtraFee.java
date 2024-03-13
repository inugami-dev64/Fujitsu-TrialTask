package com.fujitsu.fooddelivery.feeservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wpef")
@NoArgsConstructor
public class WeatherPhenomenonExtraFee extends ExtraFee {
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private WeatherPhenomenonClassification phenomenon;

    public WeatherPhenomenonExtraFee(BigDecimal extraFee, VehicleRule carRule, VehicleRule scooterRule, VehicleRule bikeRule, WeatherPhenomenonClassification phenomenon) {
        super(extraFee, carRule, scooterRule, bikeRule);
        this.phenomenon = phenomenon;
    }

    public WeatherPhenomenonExtraFee(LocalDateTime validFrom, LocalDateTime expireTime, BigDecimal extraFee, VehicleRule carRule, VehicleRule scooterRule, VehicleRule bikeRule, WeatherPhenomenonClassification phenomenon) {
        super(validFrom, expireTime, extraFee, carRule, scooterRule, bikeRule);
        this.phenomenon = phenomenon;
    }

    @Override
    public boolean matchesObservation(WeatherObservation observation) {
        return phenomenon == observation.getPhenomenon();
    }
}
