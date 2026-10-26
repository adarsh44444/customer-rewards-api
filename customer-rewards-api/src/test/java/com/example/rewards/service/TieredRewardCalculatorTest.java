package com.example.rewards.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TieredRewardCalculatorTest {
    private final TieredRewardCalculator calculator = new TieredRewardCalculator();

    @Test
    void returnsZeroBelowFifty() {
        assertEquals(0, calculator.calculate(new BigDecimal("49.99")));
    }

    @Test
    void returnsZeroAtFifty() {
        assertEquals(0, calculator.calculate(new BigDecimal("50.00")));
    }

    @Test
    void calculatesPointsBetweenFiftyAndOneHundred() {
        assertEquals(25, calculator.calculate(new BigDecimal("75.00")));
    }

    @Test
    void calculatesPointsAtOneHundred() {
        assertEquals(50, calculator.calculate(new BigDecimal("100.00")));
    }

    @Test
    void calculatesBothTiersAboveOneHundred() {
        assertEquals(90, calculator.calculate(new BigDecimal("120.00")));
    }

    @Test
    void fractionalDollarsDoNotEarnPartialPoints() {
        assertEquals(90, calculator.calculate(new BigDecimal("120.99")));
    }

    @Test
    void rejectsNegativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate(new BigDecimal("-1.00")));
    }
}
