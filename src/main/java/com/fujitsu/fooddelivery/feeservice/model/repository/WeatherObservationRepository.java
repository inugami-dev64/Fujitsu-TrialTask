package com.fujitsu.fooddelivery.feeservice.model.repository;

import com.fujitsu.fooddelivery.feeservice.model.WeatherObservation;
import com.fujitsu.fooddelivery.feeservice.model.WeatherStation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface WeatherObservationRepository extends JpaRepository<WeatherObservation, Integer> {
    Optional<WeatherObservation> findFirstByStationOrderByTimestampDesc(WeatherStation station);
    Optional<WeatherObservation> findFirstByStationAndTimestampLessThanEqualOrderByTimestampDesc(WeatherStation station, LocalDateTime timestamp);
}
