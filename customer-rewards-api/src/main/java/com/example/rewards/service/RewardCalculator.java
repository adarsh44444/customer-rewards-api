package com.example.rewards.service;

import java.math.BigDecimal;

public interface RewardCalculator {
    int calculate(BigDecimal transactionAmount);
}
