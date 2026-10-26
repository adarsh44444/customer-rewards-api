package com.example.rewards.repository;

import com.example.rewards.model.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> findById(long customerId);
    List<Customer> findAll();
}
