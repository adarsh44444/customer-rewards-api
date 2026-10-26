package com.example.rewards.model;

public record Customer(long id, String firstName, String lastName, String email) {
    public String fullName() {
        return firstName + " " + lastName;
    }
}
