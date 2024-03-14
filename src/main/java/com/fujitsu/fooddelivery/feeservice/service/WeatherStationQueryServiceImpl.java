package com.fujitsu.fooddelivery.feeservice.service;

import com.fujitsu.fooddelivery.feeservice.model.WeatherStation;
import com.fujitsu.fooddelivery.feeservice.model.repository.WeatherStationRepository;
import com.fujitsu.fooddelivery.feeservice.service.factory.WeatherApiReaderFactory;
import com.fujitsu.fooddelivery.feeservice.service.weatherapi.WeatherApiReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.List;

@Component(value = "weatherStationQueryService")
public class WeatherStationQueryServiceImpl implements WeatherStationQueryService {
    @Autowired
    private WeatherStationRepository weatherStationRepository;
    private List<WeatherApiReader> weatherApiReaders;

    public WeatherStationQueryServiceImpl() {
        updateExternalApiReaderCache();
    }

    /**
     * Updates the WeatherApiReader list so that the external API results would be relatively up-to date.<br>
     * This method is scheduled to run automatically every hour.
     */
    @Scheduled(fixedDelay = 60*60*60)
    public void updateExternalApiReaderCache() {
        WeatherApiReaderFactory factory = new WeatherApiReaderFactory();
        weatherApiReaders = factory.makeAllWeatherApiReaders();
    }

    @Override
    public WeatherStation findById(Integer id) {
        return weatherStationRepository.findById(id).orElse(null);
    }

    @Override
    public WeatherStation findByName(String name) {
        // attempt to find from the repository first
        Optional<WeatherStation> optRepoWeatherStation = weatherStationRepository.findByName(name);
        if (optRepoWeatherStation.isPresent())
            return optRepoWeatherStation.get();

        for (WeatherApiReader reader : weatherApiReaders) {
            WeatherStation station;
            if ((station = reader.findWeatherStationByName(name)) != null)
                return station;
        }

        return null;
    }
}
