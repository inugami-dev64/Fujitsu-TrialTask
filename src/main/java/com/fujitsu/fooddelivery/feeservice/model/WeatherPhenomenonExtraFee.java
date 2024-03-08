package com.fujitsu.fooddelivery.feeservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import java.util.Set;

@Entity
@Table(name = "wpef")
public class WeatherPhenomenonExtraFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Integer id;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private WeatherPhenomenonClassification phenomenon;

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

    @Getter
    @Setter
    @ManyToMany
    private Set<Location> locations;
}
