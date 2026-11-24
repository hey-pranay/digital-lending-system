package com.sprint.lending.exception;

public class BorrowerProfileNotFoundException
        extends RuntimeException {

    public BorrowerProfileNotFoundException(String message) {
        super(message);
    }
}