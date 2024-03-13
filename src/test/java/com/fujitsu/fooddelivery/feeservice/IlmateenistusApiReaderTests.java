package com.fujitsu.fooddelivery.feeservice;

import com.fujitsu.fooddelivery.feeservice.exception.ApiException;
import com.fujitsu.fooddelivery.feeservice.exception.WeatherApiException;
import com.fujitsu.fooddelivery.feeservice.exception.WeatherStationNotFoundException;
import com.fujitsu.fooddelivery.feeservice.model.WeatherStation;
import com.fujitsu.fooddelivery.feeservice.service.weatherapi.IlmateenistusApiReader;
import com.fujitsu.fooddelivery.feeservice.service.weatherapi.WeatherApiReader;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Badly written unit tests for testing IlmateenistusApiReader
 */
public class IlmateenistusApiReaderTests {
    @Test
    @DisplayName("Ensure that findWeatherStationByName() doesn't return null with valid XML values")
    void testFindWeatherStationByName_OneStationEntry_NotNull() throws DocumentException {
        final String testXml = """
            <observations timestamp=\"1337000\">
                <station>
                    <name>Kuressaare linn</name>
                    <wmocode/>
                    <longitude>22.48944444411111</longitude>
                    <latitude>58.26416666666667</latitude>
                </station>
            </observations>
            """;

        Document document = DocumentHelper.parseText(testXml);
        WeatherApiReader reader = new IlmateenistusApiReader(document);
        assertNotNull(reader.findWeatherStationByName("Kuressaare linn"));
    }

    @Test
    @DisplayName("Ensure that findWeatherStationByName() returns a WeatherStation object that contains a valid name, WMO code, and longitude/latitude")
    void testFindWeatherStationByName_OneStationEntry_CorrectNameWMOLongitudeLatitude() throws DocumentException {
        final String testXml = """
            <observations timestamp=\"1337000\">
                <station>
                    <name>Kuressaare linn</name>
                    <wmocode>1234</wmocode>
                    <longitude>22.48944444411111</longitude>
                    <latitude>58.26416666666667</latitude>
                </station>
            </observations>
            """;

        final double maxErrorMargin = 0.00001;

        Document document = DocumentHelper.parseText(testXml);
        WeatherApiReader reader = new IlmateenistusApiReader(document);
        WeatherStation station = reader.findWeatherStationByName("Kuressaare linn");
        assertEquals("Kuressaare linn", station.getName());
        assertEquals(1234, station.getWmoCode());
        assertTrue(Math.abs(station.getLongitude() - 22.48944444411111) < maxErrorMargin);
        assertTrue(Math.abs(station.getLatitude() - 58.26416666666667) < maxErrorMargin);
    }

    @Test
    @DisplayName("Ensure that findAllStations() throws a WeatherApiResponseException when station name is missing")
    void testFindAllStations_OneStationEntry_InvalidNameTag() throws DocumentException {
        final String testXml = """
            <observations timestamp=\"1337000\">
                <station>
                    <name/>
                    <wmocode>1234</wmocode>
                    <longitude>22.48944444411111</longitude>
                    <latitude>58.26416666666667</latitude>
                </station>
            </observations>
            """;

        Document document = DocumentHelper.parseText(testXml);
        WeatherApiReader reader = new IlmateenistusApiReader(document);
        assertThrows(WeatherApiException.class, reader::findAllStations);
    }

    @Test
    @DisplayName("Ensure that findAllStations() can return multiple stations specified in the XML document")
    void testFindAllStations_MultipleEntries() throws DocumentException {
        final String testXml = """
            <observations timestamp=\"1337000\">
                <station>
                    <name>Kuressaare linn</name>
                    <wmocode>1234</wmocode>
                    <longitude>22.48944444411111</longitude>
                    <latitude>58.26416666666667</latitude>
                </station>
                <station>
                    <name>Rakvere</name>
                    <wmocode>1337</wmocode>
                    <longitude/>
                    <latitude/>
                </station>
                <station>
                    <name>Tallinn-Harku</name>
                    <wmocode>777</wmocode>
                    <longitude/>
                    <latitude/>
                </station>
            </observations>
            """;

        Document document = DocumentHelper.parseText(testXml);
        WeatherApiReader reader = new IlmateenistusApiReader(document);

        List<WeatherStation> expectedWeatherStations = new ArrayList<>();
        expectedWeatherStations.add(new WeatherStation());
        expectedWeatherStations.get(expectedWeatherStations.size() - 1).setName("Kuressaare linn");
        expectedWeatherStations.get(expectedWeatherStations.size() - 1).setWmoCode(1234);
        expectedWeatherStations.get(expectedWeatherStations.size() - 1).setLongitude(Double.parseDouble("22.48944444411111"));
        expectedWeatherStations.get(expectedWeatherStations.size() - 1).setLatitude(Double.parseDouble("58.26416666666667"));

        expectedWeatherStations.add(new WeatherStation());
        expectedWeatherStations.get(expectedWeatherStations.size() - 1).setName("Rakvere");
        expectedWeatherStations.get(expectedWeatherStations.size() - 1).setWmoCode(1337);

        expectedWeatherStations.add(new WeatherStation());
        expectedWeatherStations.get(expectedWeatherStations.size() - 1).setName("Tallinn-Harku");
        expectedWeatherStations.get(expectedWeatherStations.size() - 1).setWmoCode(777);

        List<WeatherStation> actualWeatherStations;
        try {
            actualWeatherStations = reader.findAllStations();
            assertEquals(expectedWeatherStations.size(), actualWeatherStations.size());
            for (int i = 0; i < expectedWeatherStations.size(); i++) {
                assertEquals(expectedWeatherStations.get(i).getName(), actualWeatherStations.get(i).getName());
                assertEquals(expectedWeatherStations.get(i).getWmoCode(), actualWeatherStations.get(i).getWmoCode());
                assertEquals(expectedWeatherStations.get(i).getLongitude(), actualWeatherStations.get(i).getLongitude());
                assertEquals(expectedWeatherStations.get(i).getLatitude(), actualWeatherStations.get(i).getLatitude());
            }
        }
        catch (ApiException e) {
            assertFalse(false);
        }
    }

    @Test
    @DisplayName("Ensure that WeatherApiResponseException is thrown in findTheMostRecentObservationByStation() when timestamp is missing")
    void testFindTheMostRecentObservationByStation_OneStationEntry_NoTimestamp() throws DocumentException {
        final String testXml = """
            <observations>
                <station>
                    <name>Kuressaare linn</name>
                    <wmocode>1234</wmocode>
                    <longitude>22.48944444411111</longitude>
                    <latitude>58.26416666666667</latitude>
                </station>
            </observations>
            """;

        WeatherStation station = new WeatherStation();
        station.setName("Kuressaare linn");
        station.setWmoCode(1234);
        station.setLongitude(Double.parseDouble("22.48944444411111"));
        station.setLatitude(Double.parseDouble("58.26416666666667"));

        Document document = DocumentHelper.parseText(testXml);
        WeatherApiReader reader = new IlmateenistusApiReader(document);
        assertThrows(WeatherApiException.class, () -> {
            reader.findTheMostRecentObservationByStation(station);
        });
    }

    @Test
    @DisplayName("Ensure that WeatherStationNotFoundException is thrown in findTheMostRecentObservationByStation() when station does not exist in the DOM")
    void testFindTheMostRecentObservationByStation_OneStationEntry_NoStationInDOM() throws DocumentException {
        final String testXml = """
            <observations timestamp=\"1337000\">
                <station>
                    <name>Kuressaare linn</name>
                    <wmocode>1234</wmocode>
                    <longitude>22.48944444411111</longitude>
                    <latitude>58.26416666666667</latitude>
                </station>
            </observations>
            """;

        WeatherStation station = new WeatherStation();
        station.setName("Rakvere");
        station.setWmoCode(1337);
        station.setLongitude(10.0);
        station.setLatitude(10.0);

        Document document = DocumentHelper.parseText(testXml);
        WeatherApiReader reader = new IlmateenistusApiReader(document);
        assertThrows(WeatherStationNotFoundException.class, () -> {
            reader.findTheMostRecentObservationByStation(station);
        });
    }

    @Test
    @DisplayName("Ensure that WeatherApiResponseException is thrown in findTheMostRecentObservationByStation() when the air temperature data is missing")
    void testFindTheMostRecentObservationByStation_OneStationEntry_NoAirTemperature() throws DocumentException {
        final String testXml = """
            <observations timestamp=\"1337000\">
                <name>Kuressaare linn</name>
                <wmocode>1337</wmocode>
                <longitude>22.48944444411111</longitude>
                <latitude>58.26416666666667</latitude>
                <phenomenon/>
                <airtemperature/>
                <windspeed>30.7</windspeed>
            </observations>
            """;

        WeatherStation station = new WeatherStation();
        station.setName("Kuressaare linn");
        station.setWmoCode(1337);
        station.setLongitude(Double.parseDouble("22.48944444411111"));
        station.setLatitude(Double.parseDouble("58.26416666666667"));

        Document document = DocumentHelper.parseText(testXml);
        WeatherApiReader reader = new IlmateenistusApiReader(document);
        assertThrows(WeatherApiException.class, () -> {
            reader.findTheMostRecentObservationByStation(station);
        });
    }

    @Test
    @DisplayName("Ensure that WeatherApiResponseException is thrown in findTheMostRecentObservationByStation() when the wind speed data is missing")
    void testFindTheMostRecentObservationByStation_OneStationEntry_NoWindSpeed() throws DocumentException {
        final String testXml = """
            <observations timestamp=\"1337000\">
                <name>Kuressaare linn</name>
                <wmocode>1337</wmocode>
                <longitude>22.48944444411111</longitude>
                <latitude>58.26416666666667</latitude>
                <phenomenon/>
                <airtemperature>-73.8</airtemperature>
                <windspeed/>
            </observations>
            """;

        WeatherStation station = new WeatherStation();
        station.setName("Kuressaare linn");
        station.setWmoCode(1337);
        station.setLongitude(Double.parseDouble("22.48944444411111"));
        station.setLatitude(Double.parseDouble("58.26416666666667"));

        Document document = DocumentHelper.parseText(testXml);
        WeatherApiReader reader = new IlmateenistusApiReader(document);
        assertThrows(WeatherApiException.class, () -> {
            reader.findTheMostRecentObservationByStation(station);
        });
    }
}
