package com.netcracker.repository;

import com.netcracker.model.Book;
import com.netcracker.model.BookNameAndPriceOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query("SELECT DISTINCT book.name FROM Book book")
    List<String> findDistinctNames();

    @Query("SELECT DISTINCT book.price FROM Book book")
    List<Integer> findDistinctPrices();

    List<BookNameAndPriceOnly> findAllByNameContainsOrPriceGreaterThanOrderByNameAscPriceDesc(String name, int price);

}
