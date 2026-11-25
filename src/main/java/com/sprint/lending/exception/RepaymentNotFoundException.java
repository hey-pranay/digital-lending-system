package com.sprint.lending.exception;

public class RepaymentNotFoundException extends RuntimeException {

    public RepaymentNotFoundException(String message) {
        super(message);
    }
}