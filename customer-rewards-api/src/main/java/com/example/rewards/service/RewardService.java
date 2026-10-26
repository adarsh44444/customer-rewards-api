package com.example.rewards.service;

import com.example.rewards.dto.MonthlyRewardDto;
import com.example.rewards.dto.RewardSummaryDto;
import com.example.rewards.dto.TransactionRewardDto;
import com.example.rewards.exception.CustomerNotFoundException;
import com.example.rewards.model.Customer;
import com.example.rewards.model.Transaction;
import com.example.rewards.repository.CustomerRepository;
import com.example.rewards.repository.TransactionRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RewardService {
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final RewardCalculator rewardCalculator;

    public RewardService(
            CustomerRepository customerRepository,
            TransactionRepository transactionRepository,
            RewardCalculator rewardCalculator) {
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
        this.rewardCalculator = rewardCalculator;
    }

    @Cacheable(cacheNames = "rewardSummaries", key = "#customerId + ':' + #months")
    public RewardSummaryDto getRewardSummary(long customerId, int months) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.minusMonths(months - 1L).atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        List<Transaction> transactions = transactionRepository
                .findByCustomerIdAndDateRange(customerId, startDate, endDate);

        Map<YearMonth, List<TransactionRewardDto>> monthlyTransactions = new LinkedHashMap<>();
        for (int index = 0; index < months; index++) {
            monthlyTransactions.put(startDate.plusMonths(index).withDayOfMonth(1)
                    .equals(startDate) ? YearMonth.from(startDate).plusMonths(index)
                    : YearMonth.from(startDate).plusMonths(index), new ArrayList<>());
        }

        BigDecimal totalSpend = BigDecimal.ZERO;
        int totalPoints = 0;
        for (Transaction transaction : transactions) {
            int points = rewardCalculator.calculate(transaction.amount());
            YearMonth month = YearMonth.from(transaction.transactionDate());
            monthlyTransactions.computeIfAbsent(month, ignored -> new ArrayList<>())
                    .add(new TransactionRewardDto(
                            transaction.id(), transaction.transactionDate(),
                            transaction.description(), transaction.amount(), points));
            totalSpend = totalSpend.add(transaction.amount());
            totalPoints += points;
        }

        List<MonthlyRewardDto> monthlyRewards = new ArrayList<>();
        for (Map.Entry<YearMonth, List<TransactionRewardDto>> entry : monthlyTransactions.entrySet()) {
            List<TransactionRewardDto> details = List.copyOf(entry.getValue());
            BigDecimal monthlySpend = details.stream()
                    .map(TransactionRewardDto::amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            int monthlyPoints = details.stream()
                    .mapToInt(TransactionRewardDto::rewardPoints).sum();
            monthlyRewards.add(new MonthlyRewardDto(
                    entry.getKey().toString(), details.size(), monthlySpend,
                    monthlyPoints, details));
        }

        return new RewardSummaryDto(
                customer.id(), customer.fullName(), customer.email(),
                startDate, endDate, months, "USD", transactions.size(),
                totalSpend, totalPoints, List.copyOf(monthlyRewards));
    }
}
