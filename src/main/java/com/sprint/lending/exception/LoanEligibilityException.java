package com.sprint.lending.exception;

public class LoanEligibilityException
        extends RuntimeException {

    public LoanEligibilityException(String message) {
        super(message);
    }
}