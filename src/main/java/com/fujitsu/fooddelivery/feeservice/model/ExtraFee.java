package com.fujitsu.fooddelivery.feeservice.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy =  InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "extraFeeType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = AirTemperatureExtraFee.class, name = "AirTemperatureExtraFee"),
    @JsonSubTypes.Type(value = WeatherPhenomenonExtraFee.class, name = "WeatherPhenomenonExtraFee"),
    @JsonSubTypes.Type(value = WindSpeedExtraFee.class, name = "WindSpeedExtraFee")
})
public abstract class ExtraFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Integer id;

    @Getter
    @Setter
    private LocalDateTime validFrom;

    @Getter
    @Setter
    private BigDecimal extraFee;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private VehicleRule carRule;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private VehicleRule scooterRule;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private VehicleRule bikeRule;

    public ExtraFee(BigDecimal extraFee, VehicleRule carRule, VehicleRule scooterRule, VehicleRule bikeRule) {
        this.validFrom = LocalDateTime.now();
        this.extraFee = extraFee;
        this.carRule = carRule;
        this.scooterRule = scooterRule;
        this.bikeRule = bikeRule;
    }

    public ExtraFee(LocalDateTime validFrom, BigDecimal extraFee, VehicleRule carRule, VehicleRule scooterRule, VehicleRule bikeRule) {
        this.validFrom = validFrom;
        this.extraFee = extraFee;
        this.carRule = carRule;
        this.scooterRule = scooterRule;
        this.bikeRule = bikeRule;
    }

    /**
     * Checks if the given vehicle would be applicable for extra fee
     * @param vehicle type of the vehicle
     * @return VehicleRule enum that describes whether the vehicle is applicable for extra fee
     */
    public VehicleRule checkVehicleApplicability(VehicleType vehicle) {
        return switch (vehicle) {
            case CAR -> carRule;
            case SCOOTER -> scooterRule;
            case BIKE -> bikeRule;
        };
    }

    /**
     * Checks if the extra fee condition is met by weather observation
     * @param observation instance of WeatherObservation to check against
     * @return true if the weather observation matches given conditions or false if it doesn't
     */
    public abstract boolean matchesObservation(WeatherObservation observation);
}
