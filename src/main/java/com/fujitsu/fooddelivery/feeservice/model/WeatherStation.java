package com.fujitsu.fooddelivery.feeservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "weather_stations")
@NoArgsConstructor
public class WeatherStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Integer wmoCode;

    @Getter
    @Setter
    private Double longitude;

    @Getter
    @Setter
    private Double latitude;

    @Override
    public String toString() {
        return this.name;
    }
}
