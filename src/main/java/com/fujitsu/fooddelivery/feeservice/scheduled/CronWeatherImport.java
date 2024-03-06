package com.fujitsu.fooddelivery.feeservice.scheduled;

import com.fujitsu.fooddelivery.feeservice.model.repository.WeatherObservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;

@EnableAsync
public class CronWeatherImport {
    @Autowired
    private WeatherObservationRepository weatherRepository;
    private final String apiEndpoint = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    @Async
    @Scheduled(cron = "15 * * * * ?", zone = "Europe/Tallinn")
    public void scheduledWeatherDataImport() {

    }
}
