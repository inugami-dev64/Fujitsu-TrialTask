package com.fujitsu.fooddelivery.feeservice.representation;

import java.math.BigDecimal;

public record FeeResponse(BigDecimal fee, String currency) {}
