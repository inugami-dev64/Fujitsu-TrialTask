package com.fujitsu.fooddelivery.feeservice.representation.response;

public record ErrorResponse(int errorCode, String message) implements Response {}
