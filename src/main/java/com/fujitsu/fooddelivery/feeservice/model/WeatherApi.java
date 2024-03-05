package com.fujitsu.fooddelivery.feeservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="weather_apis")
@NoArgsConstructor
public class WeatherApi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    @Getter
    private Integer id;

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private String organisation;

    @Setter
    @Getter
    private String endpointUrl;

    @Setter
    @Getter
    private String country;

    @Setter
    @Getter
    private String apiType;
}
