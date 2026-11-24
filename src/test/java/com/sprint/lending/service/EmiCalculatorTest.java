package com.sprint.lending.service;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EmiCalculatorTest {

    private final EmiCalculator calculator =
            new EmiCalculator();

    @Test
    void testEmiCalculation() {

        BigDecimal result = calculator.calculateEmi(
                new BigDecimal("100000"),
                new BigDecimal("12"),
                12
        );

        assertEquals(
                new BigDecimal("8884.88"),
                result
        );
    }

    @Test
    void testZeroInterestRate() {

        BigDecimal result = calculator.calculateEmi(
                new BigDecimal("120000"),
                BigDecimal.ZERO,
                12
        );

        assertEquals(
                new BigDecimal("10000.00"),
                result
        );
    }

    @Test
    void testInvalidTenure() {

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateEmi(
                        new BigDecimal("100000"),
                        new BigDecimal("12"),
                        0
                )
        );
    }
}