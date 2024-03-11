package com.fujitsu.fooddelivery.feeservice.service;

import com.fujitsu.fooddelivery.feeservice.exception.ForbiddenVehicleException;
import com.fujitsu.fooddelivery.feeservice.model.ExtraFee;
import com.fujitsu.fooddelivery.feeservice.model.VehicleType;
import com.fujitsu.fooddelivery.feeservice.model.Location;
import com.fujitsu.fooddelivery.feeservice.model.WeatherObservation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FeeCalculationService {
    private BigDecimal pickRbf(Location location, VehicleType type) {
        return switch (type) {
            case CAR -> location.getRegionalBaseFee().getCar();
            case BIKE -> location.getRegionalBaseFee().getBike();
            case SCOOTER -> location.getRegionalBaseFee().getScooter();
        };
    }
    public BigDecimal calculate(Location location, VehicleType type, WeatherObservation observation) throws ForbiddenVehicleException {
        BigDecimal fee = pickRbf(location, type);
        if (observation == null)
            return pickRbf(location, type);

        for (ExtraFee extraFee : location.getExtraFees()) {
            if (extraFee.matchesObservation(observation)) {
                switch (extraFee.checkVehicleApplicability(type)) {
                    case APPLICABLE:
                        fee = fee.add(extraFee.getExtraFee());
                        break;

                    case FORBIDDEN:
                        throw new ForbiddenVehicleException("Usage of selected vehicle type is forbidden");

                    default:
                        break;
                }
            }
        }

        return fee;
    }
}
