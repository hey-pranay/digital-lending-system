package com.sprint.lending.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Component
public class EmiCalculator {

    private static final MathContext MC =
            MathContext.DECIMAL128;

    public BigDecimal calculateEmi(
            BigDecimal principal,
            BigDecimal annualInterestRate,
            int tenureMonths) {

        // Validate input values
        if (principal == null
                || principal.signum() <= 0
                || annualInterestRate == null
                || annualInterestRate.signum() < 0
                || tenureMonths <= 0) {

            throw new IllegalArgumentException(
                    "Invalid loan parameters"
            );
        }

        // Handle zero interest rate
        if (annualInterestRate.signum() == 0) {

            return principal.divide(
                    BigDecimal.valueOf(tenureMonths),
                    2,
                    RoundingMode.HALF_UP
            );
        }

        // Calculate monthly interest rate
        BigDecimal monthlyRate =
                annualInterestRate.divide(
                        BigDecimal.valueOf(1200),
                        MC
                );

        // Calculate (1 + r)^n
        BigDecimal factor =
                BigDecimal.ONE.add(monthlyRate)
                        .pow(tenureMonths, MC);

        // Calculate numerator
        BigDecimal numerator =
                principal.multiply(monthlyRate, MC)
                        .multiply(factor, MC);

        // Calculate denominator
        BigDecimal denominator =
                factor.subtract(BigDecimal.ONE);

        // Calculate and return monthly EMI
        return numerator.divide(
                denominator,
                2,
                RoundingMode.HALF_UP
        );
    }
}