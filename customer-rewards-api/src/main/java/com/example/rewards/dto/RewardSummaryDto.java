package com.example.rewards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RewardSummaryDto(
        long customerId,
        String customerName,
        String email,
        LocalDate periodStart,
        LocalDate periodEnd,
        int periodMonths,
        String currency,
        int totalTransactions,
        BigDecimal totalSpend,
        int totalRewardPoints,
        List<MonthlyRewardDto> monthlyRewards) {
}
