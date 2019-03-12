package com.mszychiewicz.parking;

import com.mszychiewicz.parking.payments.CostCalculator;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.mszychiewicz.parking.user.DriverType.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CostCalculatorTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void givenOneHourPeriod_whenCountCost_thenRegularValueRight() {
        // Given
        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(1, ChronoUnit.HOURS);
        // When
        double cost = CostCalculator.calculateCost(startTime, endTime, REGULAR);
        // Then
        assertThat(cost, is(1.0));
    }
    @Test
    public void givenOneHourPeriod_whenCountCost_thenDisabledValueRight() {
        // Given
        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(1, ChronoUnit.HOURS);
        // When
        double cost = CostCalculator.calculateCost(startTime, endTime, DISABLED);
        // Then
        assertThat(cost, is(0.0));
    }
    @Test
    public void givenTwoHoursPeriod_whenCountCost_thenRegularValueRight() {
        // Given
        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(2, ChronoUnit.HOURS);
        // When
        double cost = CostCalculator.calculateCost(startTime, endTime, REGULAR);
        // Then
        assertThat(cost, is(3.0));
    }
    @Test
    public void givenTwoHoursPeriod_whenCountCost_thenDisabledValueRight() {
        // Given
        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(2, ChronoUnit.HOURS);
        // When
        double cost = CostCalculator.calculateCost(startTime, endTime, DISABLED);
        // Then
        assertThat(cost, is(2.0));
    }
    @Test
    public void givenMoreThanTwoHoursPeriod_whenCountCost_thenRegularValueRight() {
        // Given
        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(5, ChronoUnit.HOURS);
        // When
        double cost = CostCalculator.calculateCost(startTime, endTime, REGULAR);
        // Then
        assertThat(cost, is(17.25));
    }
    @Test
    public void givenMoreThanTwoHoursPeriod_whenCountCost_thenDisabledValueRight() {
        // Given
        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(5, ChronoUnit.HOURS);
        // When
        double cost = CostCalculator.calculateCost(startTime, endTime, DISABLED);
        // Then
        assertThat(cost, is(10.73));
    }
}