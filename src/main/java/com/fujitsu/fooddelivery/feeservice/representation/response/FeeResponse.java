package com.fujitsu.fooddelivery.feeservice.representation.response;

import java.math.BigDecimal;

public record FeeResponse(BigDecimal fee, String currency) implements Response {}
