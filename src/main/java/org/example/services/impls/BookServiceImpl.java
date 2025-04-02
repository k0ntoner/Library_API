package org.example.services.impls;

import lombok.extern.slf4j.Slf4j;
import org.example.entities.Book;
import org.example.repositories.BookRepository;
import org.example.services.BookService;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book entity) throws SQLException {
        log.info("Request to save Book {}", entity);
        if (bookRepository.isTitleExists(entity.getTitle())) {
            throw new IllegalArgumentException("Title: {" + entity.getTitle() + "} already exists");
        }
        return bookRepository.save(entity);
    }

    @Override
    public Book findById(Long id) throws SQLException {
        log.info("Request to find Book by id {}", id);
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            return book.get();
        }
        throw new SQLException("Book with id: {" + id + "}, not found");
    }

    @Override
    public Book update(Book entity) throws SQLException {
        log.info("Request to update Book {}", entity);
        if (bookRepository.isTitleExists(entity.getTitle())) {
            throw new IllegalArgumentException("Title: {" + entity.getTitle() + "} already exists");
        }
        return bookRepository.update(entity);
    }

    @Override
    public void delete(Book entity) throws SQLException {
        log.info("Request to delete Book {}", entity);
        if (bookRepository.isCopiesOfBookBorrowed(entity.getId())) {
            throw new IllegalStateException("Copies of the book with id: {" + entity.getId() + "} is borrowed!");
        }
        bookRepository.delete(entity);
    }

    @Override
    public void deleteById(Long id) throws SQLException {
        delete(findById(id));
    }

    @Override
    public Collection<Book> findAll() throws SQLException {
        log.info("Request to find all Books");
        return bookRepository.findAll();
    }
}
