package com.example.rewards.repository;

import com.example.rewards.model.Transaction;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository {
    List<Transaction> findByCustomerIdAndDateRange(
            long customerId, LocalDate startDate, LocalDate endDate);
}
