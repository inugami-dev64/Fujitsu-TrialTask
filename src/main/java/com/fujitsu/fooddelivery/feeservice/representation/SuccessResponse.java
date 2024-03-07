package com.fujitsu.fooddelivery.feeservice.representation;
import lombok.Getter;

public class SuccessResponse {
    @Getter
    private final String message = "OK";

    @Getter
    private final int status = 200;
}
