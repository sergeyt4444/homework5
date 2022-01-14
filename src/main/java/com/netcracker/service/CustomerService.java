package com.netcracker.service;

import com.netcracker.model.Customer;
import com.netcracker.model.CustomerLastNameAndDiscountOnly;
import com.netcracker.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repository;

    public List<Customer> findAll() {
        return repository.findAll();
    }

    public List<CustomerLastNameAndDiscountOnly> findByAddress(String address) {
        return repository.findByAddressContaining(address);
    }

    public Optional<Customer> findById(Integer id) {
        return repository.findById(id);
    }

    public void delete(Customer customer) {
        repository.delete(customer);
    }

    public Customer addCustomer(Customer customer) {
        return repository.save(customer);
    }

    public List<String> findDistinctAddresses() {
        return repository.findDistinctAddresses();
    }
}
