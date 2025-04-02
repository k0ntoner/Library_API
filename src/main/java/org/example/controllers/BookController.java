package org.example.controllers;

import org.example.entities.Book;
import org.example.services.BookService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    public Collection<Book> getBooks() throws SQLException, IOException {
        return bookService.findAll();
    }

    public Book getBookById(Long id) throws SQLException, IOException {
        return bookService.findById(id);
    }

    public Book createBook(Book book) throws SQLException, IOException {
        return bookService.save(book);
    }

    public Book updateBook(Book book) throws SQLException, IOException {
        return bookService.update(book);
    }

    public void deleteBookById(Long id) throws SQLException, IOException {
        bookService.deleteById(id);
    }
}

