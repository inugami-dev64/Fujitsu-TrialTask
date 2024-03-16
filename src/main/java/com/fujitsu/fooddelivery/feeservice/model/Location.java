package com.fujitsu.fooddelivery.feeservice.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name="locations")
@Builder
@EqualsAndHashCode
@AllArgsConstructor
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rbf_id")
    @Setter
    @Getter
    @NotNull(message = "Regional base fee must be specified")
    private RegionalBaseFee regionalBaseFee;

    @OneToMany(cascade = CascadeType.ALL)
    @Setter
    @Getter
    private List<ExtraFee> extraFees;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "weather_station_id")
    @Setter
    @Getter
    @NotNull(message = "Weather station must be specified for location")
    WeatherStation weatherStation;
}
