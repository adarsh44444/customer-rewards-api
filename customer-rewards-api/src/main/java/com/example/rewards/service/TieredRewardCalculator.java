package com.example.rewards.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class TieredRewardCalculator implements RewardCalculator {
    private static final BigDecimal FIFTY = BigDecimal.valueOf(50);
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    /**
     * Whole-dollar policy: fractional dollars do not earn partial points.
     * $120.99 earns 90 points; $49.99 earns 0 points.
     */
    @Override
    public int calculate(BigDecimal transactionAmount) {
        if (transactionAmount == null || transactionAmount.signum() < 0) {
            throw new IllegalArgumentException("Transaction amount must be zero or greater.");
        }

        BigDecimal wholeDollarAmount = transactionAmount.setScale(0, RoundingMode.DOWN);
        BigDecimal firstTierDollars = wholeDollarAmount.min(ONE_HUNDRED).subtract(FIFTY)
                .max(BigDecimal.ZERO);
        BigDecimal secondTierDollars = wholeDollarAmount.subtract(ONE_HUNDRED)
                .max(BigDecimal.ZERO);

        return firstTierDollars.intValueExact() + 2 * secondTierDollars.intValueExact();
    }
}
