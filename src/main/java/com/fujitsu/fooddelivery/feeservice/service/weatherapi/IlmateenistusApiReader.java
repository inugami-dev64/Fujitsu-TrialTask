package com.fujitsu.fooddelivery.feeservice.service.weatherapi;

import com.fujitsu.fooddelivery.feeservice.exception.WeatherApiResponseException;
import com.fujitsu.fooddelivery.feeservice.exception.WeatherStationNotFoundException;
import com.fujitsu.fooddelivery.feeservice.model.WeatherObservation;
import com.fujitsu.fooddelivery.feeservice.model.WeatherStation;
import com.fujitsu.fooddelivery.feeservice.service.weatherapi.classifier.PhenomenonClassifier;
import org.dom4j.Document;
import org.dom4j.InvalidXPathException;
import org.dom4j.Node;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

public class IlmateenistusApiReader implements WeatherApiReader {
    private Logger logger;
    private Document document;
    public static final String ENDPOINT = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    public IlmateenistusApiReader(Document document) {
        this.logger = Logger.getLogger(IlmateenistusApiReader.class.getName());
        this.document = document;
    }

    @Override
    public WeatherObservation findTheMostRecentObservationByStation(WeatherStation station) throws WeatherApiResponseException, WeatherStationNotFoundException {
        logger.info("Finding the most recent observation by given weather station from respones given by Ilmateenistus XML ticker API");

        WeatherObservation observation = new WeatherObservation();
        Node stationObservation = this.document.selectSingleNode("//name[text() = '" + station.getName() + "']/..");
        if (stationObservation == null)
            throw new WeatherStationNotFoundException("Could not find weather station with name '" + station.getName() + "' from Ilmateenistus XML ticker API");

        observation.setStation(station);
        extractTimestamp(this.document.getRootElement().attributeValue("timestamp"), observation);
        extractAirTemperature(stationObservation, observation);
        extractWindSpeed(stationObservation, observation);
        extractWeatherPhenomenonOrNull(stationObservation, observation);


        return observation;
    }

    @Override
    public WeatherStation findWeatherStationByName(String name) {
        Node stationNode = document.selectSingleNode("//name[text() = '" + name + "']/..");
        if (stationNode == null)
            return null;

        WeatherStation station = new WeatherStation();
        station.setName(name);
        extractWmoCodeOrNull(stationNode, station);
        extractLongitudeOrNull(stationNode, station);
        extractLatitudeOrNull(stationNode, station);
        return station;
    }

    @Override
    public List<WeatherStation> findAllStations() throws WeatherApiResponseException {
        logger.info("Finding all stations from response given by ilmateenistus XML ticker API");
        List<WeatherStation> weatherStations = new ArrayList<>();
        List<Node> stations = document.selectNodes("//station");

        for (Node stationNode : stations) {
            WeatherStation station = new WeatherStation();
            extractName(stationNode, station);
            extractWmoCodeOrNull(stationNode, station);
            extractLongitudeOrNull(stationNode, station);
            extractLatitudeOrNull(stationNode, station);

            weatherStations.add(station);
        }

        return weatherStations;
    }

    /*
        Utility functions for extracting data from station tags
     */
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

    private void extractWeatherPhenomenonOrNull(Node station, WeatherObservation observation) {
        Node phenomenon = findSingleSubNodeOrNull(station, ".//phenomenon");
        if (phenomenon != null && !phenomenon.getText().isEmpty())
            observation.setPhenomenon(PhenomenonClassifier.classify(phenomenon.getText())); // dummy
    }

    private void extractTimestamp(String timestampAttribute, WeatherObservation observation) throws WeatherApiResponseException {
        try {
            long timestamp = Long.parseLong(timestampAttribute);
            observation.setTimestamp(LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone.getDefault().toZoneId()));
        }
        catch (NumberFormatException e) {
            throw new WeatherApiResponseException("Could not parse timestamp from weather observations API response");
        }
    }

    private void extractAirTemperature(Node station, WeatherObservation observation) throws WeatherApiResponseException {
        Node airTemperature = findSingleSubNodeOrNull(station, ".//airtemperature");
        if (airTemperature == null)
            throw new WeatherApiResponseException("<airtemperature> tag does not exist in weather station tag");

        try {
            float temperature = Float.parseFloat(airTemperature.getText());
            observation.setAirtemperature(temperature);
        }
        catch (NumberFormatException e) {
            throw new WeatherApiResponseException("Could not parse weather station's air temperature tag");
        }
    }

    private void extractWindSpeed(Node station, WeatherObservation observation) throws WeatherApiResponseException {
        Node windSpeed = findSingleSubNodeOrNull(station, ".//windspeed");
        if (windSpeed == null || windSpeed.getText().isEmpty())
            throw new WeatherApiResponseException("Could not extract wind speed from weather station");

        try {
            float wind = Float.parseFloat(windSpeed.getText());
            observation.setWindSpeed(wind);
        }
        catch (NumberFormatException e) {
            String msg = "Could not parse station's wind speed readings";
            logger.warning(msg);
            throw new WeatherApiResponseException(msg);
        }
    }

    private void extractName(Node stationNode, WeatherStation station) throws WeatherApiResponseException {
        Node nameNode = findSingleSubNodeOrNull(stationNode, ".//name");
        if (nameNode == null || nameNode.getText().isEmpty())
            throw new WeatherApiResponseException("Could not find station name from Ilmateenistus XML ticker API response");

        station.setName(nameNode.getText().trim());
    }

    private void extractWmoCodeOrNull(Node stationNode, WeatherStation station) {
        Node wmoNode = findSingleSubNodeOrNull(stationNode, ".//wmocode");
        if (wmoNode == null)
            return;

        try {
            int wmo = Integer.parseInt(wmoNode.getText().trim());
            station.setWmoCode(wmo);
        }
        catch (NumberFormatException e) {
            logger.warning("Could not parse wmo code from Ilmateenistus XML ticker API: " + e.getMessage());
        }
    }

    private void extractLongitudeOrNull(Node stationNode, WeatherStation station) {
        Node longitudeNode = findSingleSubNodeOrNull(stationNode, ".//longitude");
        if (longitudeNode == null)
            return;

        try {
            double longitude = Double.parseDouble(longitudeNode.getText().trim());
            station.setLongitude(longitude);
        }
        catch (NumberFormatException e) {
            logger.warning("Could not parse station's longitude from Ilmateenistus XML ticker API: " + e.getMessage());
        }
    }

    private void extractLatitudeOrNull(Node stationNode, WeatherStation station) {
        Node latitudeNode = findSingleSubNodeOrNull(stationNode, ".//latitude");
        if (latitudeNode == null)
            return;

        try {
            double latitude = Double.parseDouble(latitudeNode.getText().trim());
            station.setLatitude(latitude);
        }
        catch (NumberFormatException e) {
            logger.warning("Could not parse station's latitude from Ilmateenistus XML ticker API: " + e.getMessage());
        }
    }
}
