package com.fujitsu.fooddelivery.feeservice.model;

import jakarta.persistence.*;
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
    private BigDecimal car;

    @Setter
    @Getter
    private BigDecimal scooter;

    @Setter
    @Getter
    private BigDecimal bike;
}
