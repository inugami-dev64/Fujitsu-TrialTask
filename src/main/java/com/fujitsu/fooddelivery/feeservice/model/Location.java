package com.fujitsu.fooddelivery.feeservice.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name="locations")
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    @Getter
    private Integer id;

    @Setter
    @Getter
    private String country;

    @Setter
    @Getter
    private String city;

    @Setter
    @Getter
    private String currency;

    @OneToOne
    @JoinColumn(name = "rbf_id")
    @Setter
    @Getter
    private RegionalBaseFee regionalBaseFee;

    @ManyToMany
    @Setter
    @Getter
    private Set<ExtraFee> extraFees;

    @OneToOne
    @JoinColumn(name = "weather_station_id")
    @Setter
    @Getter
    WeatherStation weatherStation;
}
