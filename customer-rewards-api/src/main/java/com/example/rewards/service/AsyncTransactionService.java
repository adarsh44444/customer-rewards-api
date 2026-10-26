package com.example.rewards.service;

import com.example.rewards.model.Transaction;
import com.example.rewards.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncTransactionService {
    private static final Logger log = LoggerFactory.getLogger(AsyncTransactionService.class);
    private final TransactionRepository transactionRepository;

    public AsyncTransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Async
    public CompletableFuture<List<Transaction>> fetchTransactions(
            long customerId, LocalDate startDate, LocalDate endDate) {
        try {
            // Simulates network latency from an external transaction API.
            Thread.sleep(150);
            List<Transaction> transactions = transactionRepository
                    .findByCustomerIdAndDateRange(customerId, startDate, endDate);
            log.info("Simulated async fetch completed for customerId={}, count={}",
                    customerId, transactions.size());
            return CompletableFuture.completedFuture(transactions);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(exception);
        } catch (RuntimeException exception) {
            log.error("Simulated async fetch failed for customerId={}", customerId, exception);
            return CompletableFuture.failedFuture(exception);
        }
    }
}
