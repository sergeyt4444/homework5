package com.netcracker.service;

import com.netcracker.model.Book;
import com.netcracker.model.Shop;
import com.netcracker.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShopService {

    @Autowired
    private ShopRepository repository;

    public List<Shop> findAll() {
        return repository.findAll();
    }

    public Optional<Shop> findById(Integer id) {
        return repository.findById(id);
    }

    public List<String> findByAddressContainingSormSov() {
        return repository.findByAddressContainingSormSov();
    }

    public void delete(Shop shop) {
        repository.delete(shop);
    }

    public Shop addShop(Shop shop) {
        return repository.save(shop);
    }

}
