package com.netcracker.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.Book;
import com.netcracker.model.BookNameAndPriceOnly;
import com.netcracker.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest")
public class BookController {

    @Autowired
    BookService bookService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/books/distinct_names")
    public List<String> getAllDistinctNames() {
        return bookService.findDistinctNames();
    }

    @GetMapping("/books/distinct_prices")
    public List<Integer> getAllDistinctPrices() {
        return bookService.findDistinctPrices();
    }

    @GetMapping("/books/expensive_or_windows")
    public List<BookNameAndPriceOnly> getWindowsOrExpensive() {
        return bookService.findAllByNameContainsOrPriceGreaterThan("Windows", 20000);
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable(value = "id")Integer id) throws ResourceNotFoundException {
        Book book = bookService.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Book not found for id: " + id)
        );
        return ResponseEntity.ok(book);
    }


    @DeleteMapping("/books/{id}")
    public Map<String, Boolean> deleteBook(@PathVariable (value = "id")Integer id) throws ResourceNotFoundException {
        Book book = bookService.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Book not found for id: " + id)
        );
        bookService.delete(book);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", true);
        return response;
    }

    @PatchMapping(path = "/books/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Book> updateBook(@PathVariable Integer id, @RequestBody JsonPatch patch)
            throws ResourceNotFoundException{
        try {
            Book book = bookService.findById(id).orElseThrow(
                    () -> new ResourceNotFoundException("Book not found for id: " + id));
            Book bookPatched = applyPatchToBook(patch, book);
            bookService.addBook(bookPatched);
            return ResponseEntity.ok(bookPatched);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Book applyPatchToBook(
            JsonPatch patch, Book targetBook) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(objectMapper.convertValue(targetBook, JsonNode.class));
        return objectMapper.treeToValue(patched, Book.class);
    }

    @PostMapping("/books")
    public ResponseEntity<Book> postBook(@RequestBody Book book) {
        Book bookRes = this.bookService.addBook(book);

        return new ResponseEntity<>(bookRes, HttpStatus.OK);
    }

    @PutMapping("/books/{id}")
    Book replaceBook(@RequestBody Book book, @PathVariable Integer id) {
        return bookService.findById(id)
                .map(bookRes -> {
                    bookRes.setPrice(book.getPrice());
                    bookRes.setQuantity(book.getQuantity());
                    bookRes.setStoredAt(book.getStoredAt());
                    return bookService.addBook(bookRes);
                })
                .orElseGet(() -> {
                    return bookService.addBook(book);
                });
    }
}
