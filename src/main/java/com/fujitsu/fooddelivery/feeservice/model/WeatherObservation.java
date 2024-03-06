package com.fujitsu.fooddelivery.feeservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_observations")
@NoArgsConstructor
public class WeatherObservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String phenomenon;

    @Getter
    @Setter
    private Float airtemperature;

    @Getter
    @Setter
    private Float windSpeed;

    @Getter
    @Setter
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    @Getter
    @Setter
    private WeatherStation station;
}
