package com.example.rewards.controller;

import com.example.rewards.dto.RewardSummaryDto;
import com.example.rewards.model.Transaction;
import com.example.rewards.service.AsyncTransactionService;
import com.example.rewards.service.RewardService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1")
@Validated
public class RewardController {
    private final RewardService rewardService;
    private final AsyncTransactionService asyncTransactionService;

    public RewardController(RewardService rewardService,
                            AsyncTransactionService asyncTransactionService) {
        this.rewardService = rewardService;
        this.asyncTransactionService = asyncTransactionService;
    }

    @GetMapping("/customers/{customerId}/rewards")
    public RewardSummaryDto getRewards(
            @PathVariable @Min(1) long customerId,
            @RequestParam(defaultValue = "3") @Min(1) @Max(24) int months) {
        return rewardService.getRewardSummary(customerId, months);
    }

    @GetMapping("/customers/{customerId}/transactions/async")
    public CompletableFuture<List<Transaction>> getTransactionsAsync(
            @PathVariable @Min(1) long customerId,
            @RequestParam(defaultValue = "3") @Min(1) @Max(24) int months) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.minusMonths(months - 1L).atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();
        return asyncTransactionService.fetchTransactions(customerId, startDate, endDate);
    }
}
