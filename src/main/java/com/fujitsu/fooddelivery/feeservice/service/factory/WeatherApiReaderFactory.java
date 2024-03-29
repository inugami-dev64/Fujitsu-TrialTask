package com.fujitsu.fooddelivery.feeservice.service.factory;

import com.fujitsu.fooddelivery.feeservice.service.weatherapi.IlmateenistusApiReader;
import com.fujitsu.fooddelivery.feeservice.service.weatherapi.WeatherApiReader;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Factory class for constructing objects that implement WeatherApiReader interface
 */
public class WeatherApiReaderFactory {
    private final Logger logger;
    public WeatherApiReaderFactory() {
        this.logger = Logger.getLogger(WeatherApiProvider.class.getName());
    }

    /**
     * Constructs a new WeatherApiReader instance
     * @param provider API provider to use
     * @return a valid WeatherApiReader instance if the object construction was successful, null value otherwise
     */
    public WeatherApiReader makeWeatherApiReader(WeatherApiProvider provider) {
        if (provider == WeatherApiProvider.ILMATEENISTUS) {
            return makeIlmateenistusApiReader();
        }

        return null;
    }

    /**
     * Constructs all weather API reader implementations
     * @return a list containing all different possible WeatherApiReader implementations
     */
    public List<WeatherApiReader> makeAllWeatherApiReaders() {
        List<WeatherApiReader> weatherApiReaders = new ArrayList<>();
        weatherApiReaders.add(makeIlmateenistusApiReader());
        return weatherApiReaders;
    }

    private WeatherApiReader makeIlmateenistusApiReader() {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new URL(IlmateenistusApiReader.ENDPOINT));
            logger.info("Trying to construct Ilmateenistus API reader");
            return new IlmateenistusApiReader(document);
        }
        catch (DocumentException e) {
            logger.severe("Ilmateenistus XML ticker has returned a malformed document: " + e.getMessage());
        }
        catch (MalformedURLException e) {
            logger.severe("MalformedURLException thrown when querying for weather data from Ilmateenistus XML ticker: " + e.getMessage());
        }

        return null;
    }
}
