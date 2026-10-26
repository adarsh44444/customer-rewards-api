package com.example.rewards.repository;

import com.example.rewards.model.Transaction;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {
    private final List<Transaction> transactions = new CopyOnWriteArrayList<>();

    public InMemoryTransactionRepository() {
        // Sample data follows the current month so the default three-month query returns results.
        LocalDate currentMonth = YearMonth.now().atDay(1);
        add(1001, 101, "120.00", currentMonth.plusDays(2), "Grocery purchase");
        add(1002, 101, "75.00", currentMonth.plusDays(5), "Household items");
        add(1003, 101, "150.00", currentMonth.minusMonths(1).plusDays(10), "Electronics");
        add(1004, 101, "49.99", currentMonth.minusMonths(2).plusDays(12), "Coffee");
        add(1005, 102, "200.00", currentMonth.plusDays(1), "Home supplies");
        add(1006, 102, "90.00", currentMonth.minusMonths(1).plusDays(8), "Clothing");
        add(1007, 103, "50.00", currentMonth.minusMonths(2).plusDays(15), "Book");
        add(1008, 103, "125.00", currentMonth.minusMonths(1).plusDays(3), "Appliances");
    }

    private void add(long id, long customerId, String amount, LocalDate date, String description) {
        transactions.add(new Transaction(id, customerId, new BigDecimal(amount), date, description));
    }

    @Override
    public List<Transaction> findByCustomerIdAndDateRange(
            long customerId, LocalDate startDate, LocalDate endDate) {
        return transactions.stream()
                .filter(transaction -> transaction.customerId() == customerId)
                .filter(transaction -> !transaction.transactionDate().isBefore(startDate))
                .filter(transaction -> !transaction.transactionDate().isAfter(endDate))
                .sorted((first, second) -> {
                    int dateOrder = first.transactionDate().compareTo(second.transactionDate());
                    return dateOrder != 0 ? dateOrder : Long.compare(first.id(), second.id());
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
