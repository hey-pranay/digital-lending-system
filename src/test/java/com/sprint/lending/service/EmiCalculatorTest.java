package com.sprint.lending.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EmiCalculatorTest {

    private final EmiCalculator calculator = new EmiCalculator();

    @Test
    void testEmiCalculation() {

        BigDecimal result = calculator.calculateEmi(
                new BigDecimal("100000"),
                new BigDecimal("12"),
                12
        );

        assertEquals(new BigDecimal("8884.88"), result);
    }

    @Test
    void testZeroInterestRate() {

        BigDecimal result = calculator.calculateEmi(
                new BigDecimal("120000"),
                BigDecimal.ZERO,
                12
        );

        assertEquals(new BigDecimal("10000.00"), result);
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

    @Test
    void testNegativeTenure() {

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateEmi(
                        new BigDecimal("100000"),
                        new BigDecimal("12"),
                        -12
                )
        );
    }

    @Test
    void testNullPrincipal() {

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateEmi(
                        null,
                        new BigDecimal("12"),
                        12
                )
        );
    }

    @Test
    void testZeroPrincipal() {

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateEmi(
                        BigDecimal.ZERO,
                        new BigDecimal("12"),
                        12
                )
        );
    }

    @Test
    void testNegativePrincipal() {

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateEmi(
                        new BigDecimal("-100000"),
                        new BigDecimal("12"),
                        12
                )
        );
    }

    @Test
    void testNullInterestRate() {

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateEmi(
                        new BigDecimal("100000"),
                        null,
                        12
                )
        );
    }

    @Test
    void testNegativeInterestRate() {

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateEmi(
                        new BigDecimal("100000"),
                        new BigDecimal("-5"),
                        12
                )
        );
    }
}
