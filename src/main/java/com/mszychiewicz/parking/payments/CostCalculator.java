package com.mszychiewicz.parking.payments;

import com.mszychiewicz.parking.event.parking.ParkingEvent;
import com.mszychiewicz.parking.user.DriverType;

import java.time.Duration;
import java.time.Instant;

import static com.mszychiewicz.parking.user.DriverType.*;

public class CostCalculator {
    private final static double FIRST_HOUR_COST_REGULAR = 1.0;
    private final static double SECOND_HOUR_COST_REGULAR = 2.0;
    private final static double ANY_OTHER_HOUR_RATE_REGULAR = 1.5;

    private final static double FIRST_HOUR_COST_DISABLED = 0.0;
    private final static double SECOND_HOUR_COST_DISABLED = 2.0;
    private final static double ANY_OTHER_HOUR_RATE_DISABLED = 1.2;

    public static double calculateCost(ParkingEvent parkingEvent) {
        return calculateCost(parkingEvent.getStartTime(), parkingEvent.getEndTime(), parkingEvent.getUser().getDriverType());
    }

    public static double calculateCost(Instant startTime, Instant endTime, DriverType driverType) {
        Long minutes = Duration.between(startTime, endTime).toMinutes();
        long hours = (long) Math.ceil(minutes.doubleValue() / 60);            //calculating number of started hours
        double cost = 0.0;
        double lastHourCost = 0.0;

        cost += firstHoursCost(driverType);
        if (hours >= 2) {
            cost += lastHourCost = secondHoursCost(driverType);
        }
        for (int i = 3; i <= hours; i++) {
            cost += lastHourCost = otherHoursCost(lastHourCost, driverType);
        }
        return truncateToTwoDecimalPlaces(cost);
    }

    private static double firstHoursCost(DriverType driverType) {
        if (driverType == REGULAR) {
            return FIRST_HOUR_COST_REGULAR;
        } else {
            return FIRST_HOUR_COST_DISABLED;
        }
    }

    private static double secondHoursCost(DriverType driverType) {
        if (driverType == REGULAR) {
            return SECOND_HOUR_COST_REGULAR;
        } else {
            return SECOND_HOUR_COST_DISABLED;
        }
    }

    private static double otherHoursCost(double lastHourCost, DriverType driverType) {
        if (driverType == REGULAR) {
            return lastHourCost * ANY_OTHER_HOUR_RATE_REGULAR;
        } else {
            return lastHourCost * ANY_OTHER_HOUR_RATE_DISABLED;
        }
    }

    private static double truncateToTwoDecimalPlaces(double value){
        return Math.floor(value * 100) / 100;
    }
}
