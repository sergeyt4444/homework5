package com.netcracker.repository;

import com.netcracker.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Integer> {

    @Query("SELECT shop.name FROM Shop shop where shop.address LIKE '%Сормовский район%' or shop.address LIKE '%Советский район%'")
    List<String> findByAddressContainingSormSov();

}
