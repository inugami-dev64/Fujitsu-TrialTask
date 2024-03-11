package com.fujitsu.fooddelivery.feeservice.scheduled;

import com.fujitsu.fooddelivery.feeservice.exception.WeatherApiResponseException;
import com.fujitsu.fooddelivery.feeservice.exception.WeatherStationNotFoundException;
import com.fujitsu.fooddelivery.feeservice.model.WeatherObservation;
import com.fujitsu.fooddelivery.feeservice.model.repository.WeatherObservationRepository;
import com.fujitsu.fooddelivery.feeservice.model.repository.WeatherStationRepository;
import com.fujitsu.fooddelivery.feeservice.model.WeatherStation;

import com.fujitsu.fooddelivery.feeservice.weatherapi.IlmateenistusApiReader;
import com.fujitsu.fooddelivery.feeservice.weatherapi.WeatherApiReader;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URL;
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
        WeatherApiReader weatherApiReader = null;
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new URL(IlmateenistusApiReader.ENDPOINT));
            weatherApiReader = new IlmateenistusApiReader(document);
        }
        catch (DocumentException e) {
            logger.severe("Failed to ");
            return;
        }
        catch (MalformedURLException e) {
            logger.severe("Malformed URL expression used in WeatherAPI: " + e.getMessage());
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
