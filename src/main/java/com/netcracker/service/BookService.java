package com.netcracker.service;

import com.netcracker.model.Book;
import com.netcracker.model.BookNameAndPriceOnly;
import com.netcracker.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository repository;

    public List<Book> findAll() {
        return repository.findAll();
    }

    public Optional<Book> findById(Integer id) {
        return repository.findById(id);
    }

    public void delete(Book book) {
        repository.delete(book);
    }

    public Book addBook(Book book) {
        return repository.save(book);
    }

    public List<String> findDistinctNames() {
        return repository.findDistinctNames();
    }

    public List<Integer> findDistinctPrices() {
        return repository.findDistinctPrices();
    }

    public List<BookNameAndPriceOnly> findAllByNameContainsOrPriceGreaterThan(String name, int price) {
        return repository.findAllByNameContainsOrPriceGreaterThanOrderByNameAscPriceDesc(name,price);
    }
}
