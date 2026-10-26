package com.example.rewards.dto;

import java.math.BigDecimal;
import java.util.List;

public record MonthlyRewardDto(
        String month,
        int transactionCount,
        BigDecimal totalSpend,
        int rewardPoints,
        List<TransactionRewardDto> transactions) {
}
