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
public class AirTemperatureExtraFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Integer id;

    @Getter
    @Setter
    private LocalDateTime validFrom = LocalDateTime.now();

    @Getter
    @Setter
    private Float minTemperature;

    @Getter
    @Setter
    private Float maxTemperature;

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
    Set<Location> locations;
}
