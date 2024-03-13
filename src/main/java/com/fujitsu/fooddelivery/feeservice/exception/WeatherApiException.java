package com.fujitsu.fooddelivery.feeservice.exception;

public class WeatherApiException extends ExternalApiRequestException {
    public WeatherApiException(String msg) {
        super(msg);
    }
}
