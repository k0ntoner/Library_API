package org.example.services;

import org.example.entities.BookCopy;

import java.sql.SQLException;
import java.util.Collection;

public interface BookCopyService extends BasicService<BookCopy> {
    Collection<BookCopy> findByBookId(Long bookId) throws SQLException;

    Collection<BookCopy> findByEmail(String email) throws SQLException;
}
