package com.fujitsu.fooddelivery.feeservice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fujitsu.fooddelivery.feeservice.model.WeatherPhenomenonClassification;
import com.fujitsu.fooddelivery.feeservice.service.weatherapi.classifier.PhenomenonClassifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WeatherPhenomenonClassifierTests {
    @Test
    @DisplayName("Ensure that clear weather classification works ")
    void testClearClassification() {
        assertEquals(WeatherPhenomenonClassification.CLEAR, PhenomenonClassifier.classify(""));
        assertEquals(WeatherPhenomenonClassification.CLEAR, PhenomenonClassifier.classify("Clear"));
        assertEquals(WeatherPhenomenonClassification.CLEAR, PhenomenonClassifier.classify(null));
    }

    @Test
    @DisplayName("Ensure that cloudy weather classification works")
    void testCloudyClassification() {
        assertEquals(WeatherPhenomenonClassification.CLOUDY, PhenomenonClassifier.classify("Few clouds"));
        assertEquals(WeatherPhenomenonClassification.CLOUDY, PhenomenonClassifier.classify("Variable clouds"));
        assertEquals(WeatherPhenomenonClassification.CLOUDY, PhenomenonClassifier.classify("Cloudy with clear spills"));
        assertEquals(WeatherPhenomenonClassification.CLOUDY, PhenomenonClassifier.classify("Overcast"));
    }

    @Test
    @DisplayName("Ensure that snowy weather classification works")
    void testSnowyClassification() {
        assertEquals(WeatherPhenomenonClassification.SNOW, PhenomenonClassifier.classify("Light snow shower"));
        assertEquals(WeatherPhenomenonClassification.SNOW, PhenomenonClassifier.classify("Moderate snow shower"));
        assertEquals(WeatherPhenomenonClassification.SNOW, PhenomenonClassifier.classify("Heavy snow shower"));
        assertEquals(WeatherPhenomenonClassification.SNOW, PhenomenonClassifier.classify("Light snowfall"));
        assertEquals(WeatherPhenomenonClassification.SNOW, PhenomenonClassifier.classify("Moderate snowfall"));
        assertEquals(WeatherPhenomenonClassification.SNOW, PhenomenonClassifier.classify("Heavy snowfall"));
        assertEquals(WeatherPhenomenonClassification.SNOW, PhenomenonClassifier.classify("Blowing snow"));
        assertEquals(WeatherPhenomenonClassification.SNOW, PhenomenonClassifier.classify("Drifting snow"));
    }

    @Test
    @DisplayName("Ensure that rainy weather classification works")
    void testRainyWeatherClassification() {
        assertEquals(WeatherPhenomenonClassification.RAIN, PhenomenonClassifier.classify("Light shower"));
        assertEquals(WeatherPhenomenonClassification.RAIN, PhenomenonClassifier.classify("Moderate shower"));
        assertEquals(WeatherPhenomenonClassification.RAIN, PhenomenonClassifier.classify("Heavy shower"));
        assertEquals(WeatherPhenomenonClassification.RAIN, PhenomenonClassifier.classify("Light rain"));
        assertEquals(WeatherPhenomenonClassification.RAIN, PhenomenonClassifier.classify("Moderate rain"));
        assertEquals(WeatherPhenomenonClassification.RAIN, PhenomenonClassifier.classify("Heavy rain"));
    }

    @Test
    @DisplayName("Ensure that hail and glaze classification works")
    void testGlazyAndHailWeatherClassification() {
        assertEquals(WeatherPhenomenonClassification.HAIL, PhenomenonClassifier.classify("Hail"));
        assertEquals(WeatherPhenomenonClassification.GLAZE, PhenomenonClassifier.classify("Glaze"));
    }

    @Test
    @DisplayName("Ensure that sleet classification works")
    void testSleetWeatherClassification() {
        assertEquals(WeatherPhenomenonClassification.SLEET, PhenomenonClassifier.classify("Light sleet"));
        assertEquals(WeatherPhenomenonClassification.SLEET, PhenomenonClassifier.classify("Moderate sleet"));
    }

    @Test
    @DisplayName("Ensure that mist and fog classifications work")
    void testMistAndFogClassification() {
        assertEquals(WeatherPhenomenonClassification.MIST, PhenomenonClassifier.classify("Mist"));
        assertEquals(WeatherPhenomenonClassification.FOG, PhenomenonClassifier.classify("Fog"));
    }

    @Test
    @DisplayName("Ensure that thunder classification works")
    void testThunderClassification() {
        assertEquals(WeatherPhenomenonClassification.THUNDER, PhenomenonClassifier.classify("Thunder"));
        assertEquals(WeatherPhenomenonClassification.THUNDER, PhenomenonClassifier.classify("Thunderstorm"));
    }
}
