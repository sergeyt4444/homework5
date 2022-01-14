package com.netcracker.service;

import com.netcracker.model.Purchase;
import com.netcracker.model.PurchaseCustomerNameAndShopName;
import com.netcracker.repository.PurchaseRepository;
import com.netcracker.repository.impl.PurchaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository repository;
    @Autowired
    private PurchaseRepositoryImpl purchaseRepositoryImpl;

    public List<Purchase> findAll() {
        return repository.findAll();
    }

    public Set<String> findDistinctMonths() {
        List<String> dates = repository.findDates();
        Set<String> result = new HashSet<>();
        for (String date: dates) {
            String month = date.substring(5,7);
            result.add(month);
        }
        return result;
    }

    public List<PurchaseCustomerNameAndShopName> findAllCustomerNamesAndShopNames() {
        List<Purchase> list = repository.findAll();
        List<PurchaseCustomerNameAndShopName> result = new ArrayList<>();
        for (Purchase elem: list) {
            result.add(new PurchaseCustomerNameAndShopName(elem.getCustomer().getLastname(), elem.getShop().getName()));
        }
        return result;
    }

    public List<Object> complexPurchaseQuery() {
        return purchaseRepositoryImpl.customMethod();
    }

    public List<Object> idCustomerNameAndSumQuery() {
        return purchaseRepositoryImpl.idNameAndDateQuery();
    }

    public Optional<Purchase> findById(Integer id) {
        return repository.findById(id);
    }

    public void delete(Purchase purchase) {
        repository.delete(purchase);
    }

    public Purchase addPurchase(Purchase purchase) {
        return repository.save(purchase);
    }

    public List<Object> sameAddressQuery() {
        return purchaseRepositoryImpl.sameAddressAfterMarchQuery();
    }

    public List<Object> notAvtozavodskQuery() {
        return purchaseRepositoryImpl.notAvtoZavodskQuery();
    }

    public List<Object> boughtWhereStoredQuery() {
        return purchaseRepositoryImpl.boughtWhereStoredQuery();
    }
}
