package com.example.rewards.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Transaction(
        long id,
        long customerId,
        BigDecimal amount,
        LocalDate transactionDate,
        String description) {
}
