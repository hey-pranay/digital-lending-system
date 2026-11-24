package com.sprint.lending.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LoanRequestTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {

        factory = Validation
                .buildDefaultValidatorFactory();

        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void shouldAcceptValidLoanRequest() {

        LoanRequest request = new LoanRequest(
                new BigDecimal("100000"), 12
        );

        assertTrue(
                validator.validate(request).isEmpty()
        );
    }

    @Test
    void shouldRejectLowLoanAmount() {

        LoanRequest request = new LoanRequest(
                new BigDecimal("5000"), 12
        );

        assertFalse(
                validator.validate(request).isEmpty()
        );
    }

    @Test
    void shouldRejectHighLoanAmount() {

        LoanRequest request = new LoanRequest(
                new BigDecimal("2000000"), 12
        );

        assertFalse(
                validator.validate(request).isEmpty()
        );
    }

    @Test
    void shouldRejectNegativeLoanAmount() {

        LoanRequest request = new LoanRequest(
                new BigDecimal("-100000"), 12
        );

        assertFalse(
                validator.validate(request).isEmpty()
        );
    }

    @Test
    void shouldRejectMinimumTenureViolation() {

        LoanRequest request = new LoanRequest(
                new BigDecimal("100000"), 2
        );

        assertFalse(
                validator.validate(request).isEmpty()
        );
    }

    @Test
    void shouldRejectMaximumTenureViolation() {

        LoanRequest request = new LoanRequest(
                new BigDecimal("100000"), 72
        );

        assertFalse(
                validator.validate(request).isEmpty()
        );
    }

    @Test
    void shouldRejectNullValues() {

        LoanRequest request = new LoanRequest(
                null, null
        );

        assertEquals(
                2, validator.validate(request).size()
        );
    }
}