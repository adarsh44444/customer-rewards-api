package com.example.rewards.repository;

import com.example.rewards.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryCustomerRepository implements CustomerRepository {
    private final Map<Long, Customer> customers = Map.of(
            101L, new Customer(101L, "Alice", "Johnson", "alice@example.com"),
            102L, new Customer(102L, "Bob", "Smith", "bob@example.com"),
            103L, new Customer(103L, "Carol", "Davis", "carol@example.com")
    );

    @Override
    public Optional<Customer> findById(long customerId) {
        return Optional.ofNullable(customers.get(customerId));
    }

    @Override
    public List<Customer> findAll() {
        return List.copyOf(customers.values());
    }
}
