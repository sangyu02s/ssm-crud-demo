package com.example.ssm.service;

import com.example.ssm.domain.Book;
import com.example.ssm.mapper.BookMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Business layer.
 *
 * <p>The service layer keeps business rules and transactions out of the
 * controller. Controllers should mainly speak HTTP; services speak application logic.</p>
 */
@Service
public class BookService {
    private final BookMapper bookMapper;

    public BookService(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    // readOnly=true tells Spring this method only reads data.
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookMapper.findAll();
    }

    @Transactional(readOnly = true)
    public Book findById(Long id) {
        Book book = bookMapper.findById(id);
        if (book == null) {
            // The controller turns this exception into a 404 response.
            throw new NoSuchElementException("Book not found: " + id);
        }
        return book;
    }

    // A write operation uses a normal transaction. If an exception happens, Spring rolls it back.
    @Transactional
    public Book create(Book book) {
        validate(book);
        bookMapper.insert(book);
        return findById(book.getId());
    }

    @Transactional
    public Book update(Long id, Book book) {
        validate(book);
        book.setId(id);
        if (bookMapper.update(book) == 0) {
            throw new NoSuchElementException("Book not found: " + id);
        }
        return findById(id);
    }

    @Transactional
    public void delete(Long id) {
        if (bookMapper.deleteById(id) == 0) {
            throw new NoSuchElementException("Book not found: " + id);
        }
    }

    // Keep simple input validation close to the business operation.
    private void validate(Book book) {
        if (!StringUtils.hasText(book.getTitle())) {
            throw new IllegalArgumentException("title is required");
        }
        if (!StringUtils.hasText(book.getAuthor())) {
            throw new IllegalArgumentException("author is required");
        }
        if (book.getPrice() == null) {
            throw new IllegalArgumentException("price is required");
        }
    }
}
