package com.fujitsu.fooddelivery.feeservice.scheduled;

import com.fujitsu.fooddelivery.feeservice.model.repository.WeatherObservationRepository;
import com.fujitsu.fooddelivery.feeservice.model.repository.WeatherStationRepository;
import com.fujitsu.fooddelivery.feeservice.model.WeatherStation;
import com.fujitsu.fooddelivery.feeservice.model.WeatherObservation;

import org.dom4j.InvalidXPathException;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;

import org.dom4j.io.SAXReader;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TimeZone;
import java.util.logging.Logger;

@EnableAsync
public class CronWeatherImport {
    @Autowired
    private WeatherObservationRepository weatherObservationRepository;
    @Autowired
    private WeatherStationRepository weatherStationRepository;

    private Logger logger = Logger.getLogger(CronWeatherImport.class.getName());

    private final String apiEndpoint = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    private Node findSingleSubNodeOrNull(Node parent, String xpath) {
        Node node = null;
        try {
            node = parent.selectSingleNode(xpath);
        }
        catch (InvalidXPathException e) {
            logger.warning("XML parsing error: Could not find subnode specified with XPath '" + xpath + "' from parent '" + parent.getName() + "'");
        }

        return node;
    }

    @Async
    @Scheduled(cron = "15 * * * * ?", zone = "Europe/Tallinn")
    public void scheduledWeatherDataImport() throws MalformedURLException, DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new URL(this.apiEndpoint));
        List<Node> stations = document.selectNodes("//station");

        for (Node station : stations) {
            Node name = findSingleSubNodeOrNull(station, ".//name");

            WeatherStation weatherStation = weatherStationRepository.findByName(name.getText());
            if (weatherStation == null)
                continue;

            Node phenomenon = findSingleSubNodeOrNull(station, ".//phenomenon");
            Node airTemperature = findSingleSubNodeOrNull(station, ".//airtemperature");
            Node windSpeed = findSingleSubNodeOrNull(station, ".//windspeed");

            Long timestamp;
            try {
                timestamp = Long.parseLong(document.getRootElement().attributeValue("timestamp"));
            }
            catch (NumberFormatException e) {
                this.logger.warning("Could not read timestamp from weather API");
                timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            }

            WeatherObservation observation = new WeatherObservation();
            observation.setTimestamp(LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone.getDefault().toZoneId()));

            if (phenomenon != null && !phenomenon.getText().isEmpty())
                observation.setPhenomenon(phenomenon.getText());
            if (airTemperature != null && !airTemperature.getText().isEmpty()) {
                float temperature;
                try {
                    temperature = Float.parseFloat(airTemperature.getText());
                    observation.setAirtemperature(temperature);
                }
                catch (NumberFormatException e) {
                    this.logger.warning("Could not parse air temperature for station '" + weatherStation.getName() + "': " + e.getMessage());
                }
            }
            if (windSpeed != null && !windSpeed.getText().isEmpty()) {
                float wind;
                try {
                    wind = Float.parseFloat(windSpeed.getText());
                    observation.setWindSpeed(wind);
                }
                catch (NumberFormatException e) {
                    this.logger.warning("Could not parse wind speed for station '" + weatherStation.getName() + "': " + e.getMessage());
                }
            }

            observation.setStation(weatherStation);
            weatherObservationRepository.save(observation);
        }
    }

    public static void main(String[] args) throws MalformedURLException, DocumentException {
        CronWeatherImport weatherImport = new CronWeatherImport();
        weatherImport.scheduledWeatherDataImport();
    }
}
