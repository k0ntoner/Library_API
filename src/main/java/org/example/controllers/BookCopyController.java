package org.example.controllers;

import org.example.entities.Book;
import org.example.entities.BookCopy;
import org.example.services.BookCopyService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

public class BookCopyController {
    private final BookCopyService bookCopyService;

    public BookCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    public Collection<BookCopy> getBookCopies() throws SQLException, IOException {
        return bookCopyService.findAll();
    }

    public BookCopy getBookCopyById(Long id) throws SQLException, IOException {
        return bookCopyService.findById(id);
    }

    public BookCopy createBookCopy(BookCopy bookCopy) throws SQLException, IOException {
        return bookCopyService.save(bookCopy);
    }

    public BookCopy updateBookCopy(BookCopy bookCopy) throws SQLException, IOException {
        return bookCopyService.update(bookCopy);
    }

    public void deleteBookCopyById(Long id) throws SQLException, IOException {
        bookCopyService.deleteById(id);
    }
}
