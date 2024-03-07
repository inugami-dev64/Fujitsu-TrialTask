package com.fujitsu.fooddelivery.feeservice.scheduled;

import com.fujitsu.fooddelivery.feeservice.exception.WeatherApiException;
import com.fujitsu.fooddelivery.feeservice.exception.WeatherApiResponseException;
import com.fujitsu.fooddelivery.feeservice.exception.WeatherStationNotFoundException;
import com.fujitsu.fooddelivery.feeservice.model.WeatherObservation;
import com.fujitsu.fooddelivery.feeservice.model.repository.WeatherObservationRepository;
import com.fujitsu.fooddelivery.feeservice.model.repository.WeatherStationRepository;
import com.fujitsu.fooddelivery.feeservice.model.WeatherStation;

import com.fujitsu.fooddelivery.feeservice.weatherapi.IlmateenistusApi;
import com.fujitsu.fooddelivery.feeservice.weatherapi.WeatherAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
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

    public CronWeatherImport() throws MalformedURLException {
        this.logger = Logger.getLogger(CronWeatherImport.class.getName());
    }

    @Async
    @Scheduled(cron = "0 15 * * * ?", zone = "Europe/Tallinn")
    public void scheduledWeatherDataImport() {
        logger.info("Performing a scheduled weather data import");
        List<WeatherStation> stations = weatherStationRepository.findAll();
        WeatherAPI weatherApi = null;
        try {
            weatherApi = new IlmateenistusApi();
        }
        catch (WeatherApiResponseException e) {
            logger.severe("Could not create WeatherAPI object: " + e.getMessage());
            return;
        }
        catch (MalformedURLException e) {
            logger.severe("Malformed URL expression used in WeatherAPI: " + e.getMessage());
            return;
        }

        for (WeatherStation station : stations) {
            try {
                WeatherObservation observation = weatherApi.findTheMostRecentObservationByStation(station);
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
