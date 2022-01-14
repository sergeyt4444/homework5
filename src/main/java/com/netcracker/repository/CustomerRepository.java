package com.netcracker.repository;


import com.netcracker.model.Customer;
import com.netcracker.model.CustomerLastNameAndDiscountOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query("SELECT DISTINCT customer.address FROM Customer customer")
    List<String> findDistinctAddresses();

    List<CustomerLastNameAndDiscountOnly> findByAddressContaining(String address);

//    List<LastNameAndDiscountOnly> findByAddressContaining(String address);

}