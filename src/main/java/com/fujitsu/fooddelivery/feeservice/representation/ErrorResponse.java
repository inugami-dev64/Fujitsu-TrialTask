package com.fujitsu.fooddelivery.feeservice.representation;

public record ErrorResponse(String errorMessage, Integer httpCode) {}
