package com.fujitsu.fooddelivery.feeservice.service.weatherapi.classifier;

import com.fujitsu.fooddelivery.feeservice.model.WeatherPhenomenonClassification;
import java.util.regex.Pattern;

public class PhenomenonClassifier {
    public static WeatherPhenomenonClassification classify(String phenomenon) {
        if (phenomenon == null)
            return WeatherPhenomenonClassification.CLEAR;

        Pattern p = Pattern.compile("^.*(cloud.*|overcast).*$", Pattern.CASE_INSENSITIVE);
        if (p.matcher(phenomenon).matches())
            return WeatherPhenomenonClassification.CLOUDY;

        p = Pattern.compile("^[A-Za-z]+ snow.*$", Pattern.CASE_INSENSITIVE);
        if (p.matcher(phenomenon).matches())
            return WeatherPhenomenonClassification.SNOW;

        p = Pattern.compile("^[A-Za-z]+ (shower|rain).*$", Pattern.CASE_INSENSITIVE);
        if (p.matcher(phenomenon).matches())
            return WeatherPhenomenonClassification.RAIN;

        p = Pattern.compile("^.*glaze.*$", Pattern.CASE_INSENSITIVE);
        if (p.matcher(phenomenon).matches())
            return WeatherPhenomenonClassification.GLAZE;

        p = Pattern.compile("^.*sleet.*$", Pattern.CASE_INSENSITIVE);
        if (p.matcher(phenomenon).matches())
            return WeatherPhenomenonClassification.SLEET;

        p = Pattern.compile("^.*hail.*$", Pattern.CASE_INSENSITIVE);
        if (p.matcher(phenomenon).matches())
            return WeatherPhenomenonClassification.HAIL;

        p = Pattern.compile("^.*mist.*$", Pattern.CASE_INSENSITIVE);
        if (p.matcher(phenomenon).matches())
            return WeatherPhenomenonClassification.MIST;

        p = Pattern.compile("^.*fog.*$", Pattern.CASE_INSENSITIVE);
        if (p.matcher(phenomenon).matches())
            return WeatherPhenomenonClassification.FOG;

        p = Pattern.compile("^.*thunder.*$", Pattern.CASE_INSENSITIVE);
        if (p.matcher(phenomenon).matches())
            return WeatherPhenomenonClassification.THUNDER;

        return WeatherPhenomenonClassification.CLEAR;
    }
}
