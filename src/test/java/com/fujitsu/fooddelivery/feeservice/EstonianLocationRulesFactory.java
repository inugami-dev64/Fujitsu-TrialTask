package com.fujitsu.fooddelivery.feeservice;

import com.fujitsu.fooddelivery.feeservice.model.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is used as factory class that produces Estonian locations and its rules as specified in the requirements document.<br>
 * Regional base fee by city:<br>
 *   &emsp;Tallinn - car: 4€, scooter: 3.5€, bike: 3€<br>
 *   &emsp;Tartu - car: 3.5€, scooter: 3€, bike: 2.5€<br>
 *   &emsp;Pärnu - car: 3€, scooter: 2.5€, bike: 2€<br>
 * Extra fees are applied to all cities under equal amounts.<br>
 * Air temperature extra fee (applies to scooter and bike couriers):<br>
 *   &emsp;Air temperature is less than -10C - 1€<br>
 *   &emsp;Air temperature is between -10C and 0C - 0.5€<br>
 * Wind speed extra fee (applies to bike couriers):<br>
 *   &emsp;Wind speed is between 10 m/s and 20 m/s - 0.5€<br>
 *   &emsp;Wind speed is greater than 20 m/s - forbidden<br>
 * Weather phenomenon extra fee (applies to scooter and bike couriers):<br>
 *   &emsp;Weather phenomenon is related to snow or sleet - 1€<br>
 *   &emsp;Weather phenomenon is related to rain - 0.5€<br>
 *   &emsp;Weather phenomenon is related to glaze, hail, thunder - forbidden
 */
public class EstonianLocationRulesFactory {
    private static Set<ExtraFee> makeExtraFeeRules() {
        Set<ExtraFee> extraFees = new HashSet<>();
        extraFees.add(new AirTemperatureExtraFee(new BigDecimal("1.00"), VehicleRule.NOT_APPLICABLE, VehicleRule.APPLICABLE, VehicleRule.APPLICABLE, null, -10.f));
        extraFees.add(new AirTemperatureExtraFee(new BigDecimal("0.50"), VehicleRule.NOT_APPLICABLE, VehicleRule.APPLICABLE, VehicleRule.APPLICABLE, -10.f, 0.f));
        extraFees.add(new WindSpeedExtraFee(new BigDecimal("0.50"), VehicleRule.NOT_APPLICABLE, VehicleRule.NOT_APPLICABLE, VehicleRule.APPLICABLE, 10.f, 20.f));
        extraFees.add(new WindSpeedExtraFee(new BigDecimal("0.00"), VehicleRule.NOT_APPLICABLE, VehicleRule.NOT_APPLICABLE, VehicleRule.FORBIDDEN, 20.f, null));
        extraFees.add(new WeatherPhenomenonExtraFee(new BigDecimal("1.00"), VehicleRule.NOT_APPLICABLE, VehicleRule.APPLICABLE, VehicleRule.APPLICABLE, WeatherPhenomenonClassification.SNOW));
        extraFees.add(new WeatherPhenomenonExtraFee(new BigDecimal("1.00"), VehicleRule.NOT_APPLICABLE, VehicleRule.APPLICABLE, VehicleRule.APPLICABLE, WeatherPhenomenonClassification.SLEET));
        extraFees.add(new WeatherPhenomenonExtraFee(new BigDecimal("0.50"), VehicleRule.NOT_APPLICABLE, VehicleRule.APPLICABLE, VehicleRule.APPLICABLE, WeatherPhenomenonClassification.RAIN));
        extraFees.add(new WeatherPhenomenonExtraFee(new BigDecimal("0.00"), VehicleRule.NOT_APPLICABLE, VehicleRule.FORBIDDEN, VehicleRule.FORBIDDEN, WeatherPhenomenonClassification.GLAZE));
        extraFees.add(new WeatherPhenomenonExtraFee(new BigDecimal("0.00"), VehicleRule.NOT_APPLICABLE, VehicleRule.FORBIDDEN, VehicleRule.FORBIDDEN, WeatherPhenomenonClassification.HAIL));
        extraFees.add(new WeatherPhenomenonExtraFee(new BigDecimal("0.00"), VehicleRule.NOT_APPLICABLE, VehicleRule.FORBIDDEN, VehicleRule.FORBIDDEN, WeatherPhenomenonClassification.THUNDER));
        return extraFees;
    }

    private static Location makeNewLocation(final String city) {
        Location location = new Location();
        location.setCity(city);
        location.setCountry("Estonia");
        location.setCurrency("EUR");

        return location;
    }

    private static RegionalBaseFee makeRegionalBaseFee(String car, String scooter, String bike) {
        RegionalBaseFee rbf = new RegionalBaseFee();
        rbf.setCar(new BigDecimal(car));
        rbf.setScooter(new BigDecimal(scooter));
        rbf.setBike(new BigDecimal(bike));

        return rbf;
    }


    public static Location makeTallinnLocation() {
        Location tallinn = makeNewLocation("Tallinn");
        tallinn.setRegionalBaseFee(makeRegionalBaseFee("4.00", "3.50", "3.00"));
        tallinn.setExtraFees(makeExtraFeeRules());
        return tallinn;
    }

    public static Location makeTartuLocation() {
        Location tartu = makeNewLocation("Tartu");
        tartu.setRegionalBaseFee(makeRegionalBaseFee("3.50", "3.00", "2.50"));
        tartu.setExtraFees(makeExtraFeeRules());
        return tartu;
    }

    public static Location makeParnuLocation() {
        Location parnu = makeNewLocation("Parnu");
        parnu.setRegionalBaseFee(makeRegionalBaseFee("3.00", "2.50", "2.00"));
        parnu.setExtraFees(makeExtraFeeRules());
        return parnu;
    }
}
