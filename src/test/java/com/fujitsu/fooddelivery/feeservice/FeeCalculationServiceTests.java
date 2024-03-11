package com.fujitsu.fooddelivery.feeservice;

import com.fujitsu.fooddelivery.feeservice.exception.ForbiddenVehicleException;
import com.fujitsu.fooddelivery.feeservice.model.*;
import com.fujitsu.fooddelivery.feeservice.service.FeeCalculationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class FeeCalculationServiceTests {
    @Test
    @DisplayName("Ensure that the example calculation is correct")
    void testCalculation_TartuBike_StatementExampleParameters() {
        final VehicleType type = VehicleType.BIKE;

        Location location = EstonianLocationRulesFactory.makeTartuLocation();

        WeatherObservation observation = new WeatherObservation();
        observation.setAirtemperature(-2.1f);
        observation.setWindSpeed(4.7f);
        observation.setPhenomenon(WeatherPhenomenonClassification.SNOW);

        FeeCalculationService service = new FeeCalculationService();
        try {
            BigDecimal fee = service.calculate(location, type, observation);
            assertEquals(new BigDecimal("4.00"), fee);
        }
        catch (ForbiddenVehicleException e) {
            fail();
        }
    }

    @Test
    @DisplayName("Ensure that scooter and bike maximum possible values can be calculated correctly")
    void testCalculation_TallinnScooter_Maximum() {
        Location location = EstonianLocationRulesFactory.makeTallinnLocation();
        WeatherObservation observation = new WeatherObservation();
        observation.setAirtemperature(-15.f);
        observation.setWindSpeed(15.f);
        observation.setPhenomenon(WeatherPhenomenonClassification.SNOW);

        FeeCalculationService service = new FeeCalculationService();
        try {
            final BigDecimal scooterFee = service.calculate(location, VehicleType.SCOOTER, observation);
            assertEquals(new BigDecimal("5.50"), scooterFee);
            final BigDecimal bikeFee = service.calculate(location, VehicleType.BIKE, observation);
            assertEquals(new BigDecimal("5.50"), bikeFee);
        }
        catch (ForbiddenVehicleException e) {
            fail();
        }
    }

    @Test
    @DisplayName("Ensure that forbidden condition (wind speed greater than 20 m/s for bikes) throws ForbiddenVehicleException")
    void testCalculation_TallinnBike_WindSpeedTooStrong() {
        Location location = EstonianLocationRulesFactory.makeTallinnLocation();
        WeatherObservation observation = new WeatherObservation();
        observation.setAirtemperature(-2.1f);
        observation.setWindSpeed(21.2f);
        observation.setPhenomenon(WeatherPhenomenonClassification.CLOUDY);

        FeeCalculationService service = new FeeCalculationService();
        assertThrows(ForbiddenVehicleException.class, () -> {
            service.calculate(location, VehicleType.BIKE, observation);
        });
    }

    @Test
    @DisplayName("Ensure that forbidden condition (weather phenomenon is glaze for bikes and scooters) throws ForbiddenVehicleException")
    void testCalculation_TallinnBikeAndScooter_Glaze() {
        Location location = EstonianLocationRulesFactory.makeTallinnLocation();
        WeatherObservation observation = new WeatherObservation();
        observation.setAirtemperature(15.9f);
        observation.setWindSpeed(5.1f);
        observation.setPhenomenon(WeatherPhenomenonClassification.THUNDER);

        FeeCalculationService service = new FeeCalculationService();
        assertThrows(ForbiddenVehicleException.class, () -> {
            service.calculate(location, VehicleType.BIKE, observation);
        });
        assertThrows(ForbiddenVehicleException.class, () -> {
            service.calculate(location, VehicleType.SCOOTER, observation);
        });
    }
}
