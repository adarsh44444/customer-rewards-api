package com.example.rewards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRewardDto(
        long transactionId,
        LocalDate transactionDate,
        String description,
        BigDecimal amount,
        int rewardPoints) {
}
