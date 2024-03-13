package com.fujitsu.fooddelivery.feeservice.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    @NotNull(message = "Country name must be specified")
    private String country;

    @Setter
    @Getter
    @NotNull(message = "City name must be specified")
    private String city;

    @Setter
    @Getter
    @NotNull(message = "Currency must be specified")
    private String currency;

    @OneToOne
    @JoinColumn(name = "rbf_id")
    @Setter
    @Getter
    @NotNull(message = "Regional base fee must be specified")
    private RegionalBaseFee regionalBaseFee;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    @Setter
    @Getter
    private List<ExtraFee> extraFees;

    @OneToOne
    @JoinColumn(name = "weather_station_id")
    @Setter
    @Getter
    @NotNull(message = "Weather station must be specified for location")
    WeatherStation weatherStation;
}
