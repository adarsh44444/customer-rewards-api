package com.example.rewards.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(long customerId) {
        super("Customer " + customerId + " was not found.");
    }
}
