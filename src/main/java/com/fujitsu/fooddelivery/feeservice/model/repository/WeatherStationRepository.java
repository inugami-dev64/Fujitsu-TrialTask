package com.fujitsu.fooddelivery.feeservice.model.repository;

import com.fujitsu.fooddelivery.feeservice.model.WeatherStation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherStationRepository extends JpaRepository<WeatherStation, Integer> {
    WeatherStation findByName(String name);
}
