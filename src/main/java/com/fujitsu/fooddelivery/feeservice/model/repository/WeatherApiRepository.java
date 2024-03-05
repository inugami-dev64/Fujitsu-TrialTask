package com.fujitsu.fooddelivery.feeservice.model.repository;

import com.fujitsu.fooddelivery.feeservice.model.WeatherApi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeatherApiRepository extends JpaRepository<WeatherApi, Integer> {
    List<WeatherApi> findByCountry(String country);
    WeatherApi findByName(String name);
    WeatherApi findByOrganisation(String name);
}
