package com.fujitsu.fooddelivery.feeservice.service;

import com.fujitsu.fooddelivery.feeservice.exception.WeatherApiResponseException;
import com.fujitsu.fooddelivery.feeservice.exception.WeatherStationNotFoundException;
import com.fujitsu.fooddelivery.feeservice.model.WeatherObservation;
import com.fujitsu.fooddelivery.feeservice.model.repository.WeatherObservationRepository;
import com.fujitsu.fooddelivery.feeservice.model.repository.WeatherStationRepository;
import com.fujitsu.fooddelivery.feeservice.model.WeatherStation;

import com.fujitsu.fooddelivery.feeservice.service.factory.WeatherApiProvider;
import com.fujitsu.fooddelivery.feeservice.service.factory.WeatherApiReaderFactory;
import com.fujitsu.fooddelivery.feeservice.service.weatherapi.WeatherApiReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

@EnableAsync
@Component
public class CronWeatherImport {
    @Autowired
    private WeatherObservationRepository weatherObservationRepository;
    @Autowired
    private WeatherStationRepository weatherStationRepository;
    private Logger logger = Logger.getLogger(CronWeatherImport.class.getName());

    public CronWeatherImport() {
        this.logger = Logger.getLogger(CronWeatherImport.class.getName());
    }

    @Async
    @Scheduled(cron = "0 15 * * * ?", zone = "Europe/Tallinn")
    public void scheduledWeatherDataImport() {
        logger.info("Performing a scheduled weather data import");
        List<WeatherStation> stations = weatherStationRepository.findAll();
        WeatherApiReaderFactory apiReaderFactory = new WeatherApiReaderFactory();
        WeatherApiReader weatherApiReader = apiReaderFactory.makeWeatherApiReader(WeatherApiProvider.ILMATEENISTUS);
        if (weatherApiReader == null) {
            logger.severe("Failed to perform scheduled weather data import");
            return;
        }

        for (WeatherStation station : stations) {
            try {
                WeatherObservation observation = weatherApiReader.findTheMostRecentObservationByStation(station);
                weatherObservationRepository.save(observation);
            }
            catch (WeatherStationNotFoundException e) {
                logger.warning(e.getMessage());
                logger.warning("Skipping weather observation update for '" + station.getName() + "'");
            }
            catch (WeatherApiResponseException e) {
                logger.warning(e.getMessage());
                logger.warning("This could indicate a problem with integrity of data from the external API");
            }
        }
    }
}
