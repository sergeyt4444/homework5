package com.netcracker.repository;

import com.netcracker.model.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Integer>, CustomPurchaseRepository {

    @Query("SELECT purchase.date FROM Purchase purchase")
    List<String> findDates();

}
