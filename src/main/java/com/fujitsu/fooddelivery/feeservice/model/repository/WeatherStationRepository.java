package com.fujitsu.fooddelivery.feeservice.model.repository;

import com.fujitsu.fooddelivery.feeservice.model.WeatherStation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WeatherStationRepository extends JpaRepository<WeatherStation, Integer> {
    boolean existsByName(String name);
    Optional<WeatherStation> findByName(String name);
    Optional<WeatherStation> findByWmoCode(Integer wmo);
}
