package com.fujitsu.fooddelivery.feeservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "rbf")
@NoArgsConstructor
public class RegionalBaseFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    @Getter
    private Integer id;

    @Setter
    @Getter
    @NotNull(message = "Car courier base fee must be specified")
    private BigDecimal car;

    @Setter
    @Getter
    @NotNull(message = "Scooter courier base fee must be specified")
    private BigDecimal scooter;

    @Setter
    @Getter
    @NotNull(message = "Bike courier base fee must be specified")
    private BigDecimal bike;
}
