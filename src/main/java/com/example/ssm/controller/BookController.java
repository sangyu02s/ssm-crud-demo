package com.example.ssm.controller;

import com.example.ssm.domain.Book;
import com.example.ssm.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * REST API layer.
 *
 * <p>The controller receives HTTP requests, reads path/body data, calls the
 * service layer, and returns Java objects that Spring converts to JSON.</p>
 */
@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // GET /api/books: query all books.
    @GetMapping
    public List<Book> findAll() {
        return bookService.findAll();
    }

    // GET /api/books/{id}: query one book by primary key.
    @GetMapping("/{id}")
    public Book findById(@PathVariable("id") Long id) {
        return bookService.findById(id);
    }

    // POST /api/books with JSON request body: create a new book.
    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book book) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(book));
    }

    // PUT /api/books/{id} with JSON request body: replace title/author/price for an existing book.
    @PutMapping("/{id}")
    public Book update(@PathVariable("id") Long id, @RequestBody Book book) {
        return bookService.update(id, book);
    }

    // DELETE /api/books/{id}: delete a book. 204 means success with no response body.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Convert "not found" service exceptions into HTTP 404 JSON responses.
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }

    // Convert validation failures into HTTP 400 JSON responses.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }
}
