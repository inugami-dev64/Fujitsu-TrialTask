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
public class WindSpeedExtraFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Integer id;

    @Getter
    @Setter
    private LocalDateTime validFrom = LocalDateTime.now();

    @Getter
    @Setter
    private Float minWindSpeed;

    @Getter
    @Setter
    private Float maxWindSpeed;

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
